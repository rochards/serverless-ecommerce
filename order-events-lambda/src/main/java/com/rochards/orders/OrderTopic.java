package com.rochards.orders;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderTopic {

    private EventType type;
    private OrderEvent orderEvent;

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEvent {
        private String lambdaRequestId;
        private String email;
        private String orderId;
        private Shipping shipping;
        private Billing billing;
        private List<String> productCodes;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Shipping {
        private ShippingType type;
        private Carrier carrier;
    }

    public enum Carrier {
        CORREIOS, FEDEX
    }

    public enum ShippingType {
        URGENT, ECONOMIC
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Billing {
        private PaymentMethod paymentMethod;
        private BigDecimal totalPrice;
    }

    public enum PaymentMethod {
        CASH, DEBIT_CARD, CREDIT_CARD
    }
}
