package com.rochards.orders;

import com.rochards.product.ProductModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OrderParse {

    private OrderParse() {
    }

    public static OrderModel requestToModel(OrderRequest orderRequest, List<ProductModel> productModels) {
        var order = new OrderModel();
        order.setEmail(orderRequest.getEmail());
        order.setCreatedAt(Instant.now().toEpochMilli());
        order.setShipping(
                new OrderModel.Shipping(orderRequest.getShipping().getType(), orderRequest.getShipping().getCarrier())
        );
        order.setBilling(
                new OrderModel.Billing(orderRequest.getPaymentMethod(), sumProductsPrice(productModels))
        );
        order.setProducts(buildOrderModelProducts(productModels));

        return order;
    }

    private static List<OrderModel.Product> buildOrderModelProducts(List<ProductModel> productModels) {
        return productModels.stream()
                .map(product -> new OrderModel.Product(product.getId(), product.getCode(), product.getPrice()))
                .collect(Collectors.toList());
    }

    private static BigDecimal sumProductsPrice(List<ProductModel> productModels) {
        return productModels.stream()
                .map(ProductModel::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static OrderResponse modelToResponse(OrderModel order) {
        var orderResponse = new OrderResponse();
        orderResponse.setId(order.getOrderId());
        orderResponse.setEmail(order.getEmail());
        orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setBilling(
                new OrderResponse.Billing(order.getBilling().getPaymentMethod(), order.getBilling().getTotalPrice())
        );
        orderResponse.setShipping(
                new Shipping(order.getShipping().getType(), order.getShipping().getCarrier())
        );
        orderResponse.setProducts(buildProductsResponse(order.getProducts()));

        return orderResponse;
    }

    private static List<OrderResponse.Product> buildProductsResponse(List<OrderModel.Product> products) {
        return products.stream()
                .map(orderProduct -> new OrderResponse.Product(orderProduct.getId(), orderProduct.getCode(), orderProduct.getPrice()))
                .collect(Collectors.toList());
    }
}
