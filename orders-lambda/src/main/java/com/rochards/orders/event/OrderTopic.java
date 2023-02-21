package com.rochards.orders.event;

import com.rochards.orders.Billing;
import com.rochards.orders.Shipping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTopic {
    private Type type;
    private OrderEvent orderEvent;

    public enum Type {
        ORDER_CREATED, ORDER_DELETED
    }

    @Getter
    @Builder
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
}
