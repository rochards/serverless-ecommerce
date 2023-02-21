package com.rochards.orders;

import java.util.List;

public class OrderEvent {

    private String requestId;
    private String email;
    private String orderId;
    private Shipping shipping;
    private Billing billing;
    private List<String> productCodes;
}
