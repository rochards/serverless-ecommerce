package com.rochards.orders.event;

import com.rochards.orders.OrderResponse;

import java.util.List;
import java.util.stream.Collectors;

public class OrderEventParser {

    private OrderEventParser() {
    }

    public static OrderTopic responseToTopic(OrderResponse order, OrderTopic.Type eventType, String lambdaRequestId) {
        var orderEvent = OrderTopic.OrderEvent.builder()
                .lambdaRequestId(lambdaRequestId)
                .email(order.getEmail())
                .orderId(order.getId())
                .shipping(order.getShipping())
                .billing(order.getBilling())
                .productCodes(getProductCodes(order.getProducts()))
                .build();

        return OrderTopic.builder()
                .type(eventType)
                .orderEvent(orderEvent)
                .build();
    }

    private static List<String> getProductCodes(List<OrderResponse.Product> products) {
        return products.stream()
                .map(OrderResponse.Product::getCode)
                .collect(Collectors.toList());
    }
}
