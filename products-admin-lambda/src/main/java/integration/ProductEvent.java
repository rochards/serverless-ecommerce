package integration;

import java.math.BigDecimal;

public class ProductEvent {

    private String requestId;
    private EventType eventType;
    private String productId;
    private String productCode;
    private BigDecimal productPrice;
    private String email;

    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}
