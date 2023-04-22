package com.rochards.orders;

public class OrdersEventsParser {

    private OrdersEventsParser() {}

    public static OrderEventResponse toResponse(OrderEventModel model) {
        return OrderEventResponse.builder()
                .email(model.getEmail())
                .code(model.getCode())
                .type(model.getEventType())
                .createdAt(model.getCreatedAt())
                .orderId(model.getInfo().getOrderId())
                .productCodes(model.getInfo().getProductCodes())
                .build();
    }
}
