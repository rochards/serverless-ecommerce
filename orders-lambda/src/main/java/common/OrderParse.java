package common;

import orders.OrderModel;
import orders.OrderRequest;
import orders.OrderResponse;
import orders.PaymentMethod;
import orders.Shipping;
import product.ProductModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OrderParse {

    public static OrderModel requestToModel(OrderRequest orderRequest, List<ProductModel> productModels) {
        var order = new OrderModel();
        order.setEmail(orderRequest.getEmail());
        order.setCreatedAt(Instant.now().toEpochMilli());
        order.setShipping(buildShippingModel(orderRequest.getShipping()));
        order.setBilling(buildBillingModel(orderRequest.getPaymentMethod(), sumProductsPrice(productModels)));
        order.setProducts(buildOrderModelProducts(productModels));

        return order;
    }

    private static OrderModel.Shipping buildShippingModel(Shipping shipping) {
        var orderShipping = new OrderModel.Shipping();
        orderShipping.setType(shipping.getType());
        orderShipping.setCarrier(shipping.getCarrier());

        return orderShipping;
    }

    private static OrderModel.Billing buildBillingModel(PaymentMethod paymentMethod, BigDecimal totalPrice) {
        var billingModel = new OrderModel.Billing();
        billingModel.setPaymentMethod(paymentMethod);
        billingModel.setTotalPrice(totalPrice);

        return billingModel;
    }

    private static List<OrderModel.Product> buildOrderModelProducts(List<ProductModel> productModels) {
        return productModels.stream()
                .map(product -> {
                    var orderModelProduct = new OrderModel.Product();
                    orderModelProduct.setId(product.getId());
                    orderModelProduct.setCode(product.getCode());
                    orderModelProduct.setPrice(product.getPrice());

                    return orderModelProduct;
                })
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
        orderResponse.setBilling(buildBillingResponse(order.getBilling()));
        orderResponse.setShipping(buildShippingResponse(order.getShipping()));
        orderResponse.setProducts(buildProductsResponse(order.getProducts()));

        return orderResponse;
    }

    private static OrderResponse.Billing buildBillingResponse(OrderModel.Billing billing) {
        var responseBilling = new OrderResponse.Billing();
        responseBilling.setPaymentMethod(billing.getPaymentMethod());
        responseBilling.setTotalPrice(billing.getTotalPrice());

        return responseBilling;
    }

    private static Shipping buildShippingResponse(OrderModel.Shipping shipping) {
        var shippingResponse = new Shipping();
        shippingResponse.setType(shipping.getType());
        shippingResponse.setCarrier(shipping.getCarrier());

        return shippingResponse;
    }

    private static List<OrderResponse.Product> buildProductsResponse(List<OrderModel.Product> products) {
        return products.stream()
                .map(orderProduct -> {
                    var productResponse = new OrderResponse.Product();
                    productResponse.setId(orderProduct.getId());
                    productResponse.setCode(orderProduct.getCode());
                    productResponse.setPrice(orderProduct.getPrice());

                    return productResponse;
                })
                .collect(Collectors.toList());
    }
}
