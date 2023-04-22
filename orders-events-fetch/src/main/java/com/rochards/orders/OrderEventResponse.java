package com.rochards.orders;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderEventResponse {

    private String email;
    private String code;
    private EventType type;
    private long createdAt;
    private String orderId;
    private List<String> productCodes;
}
