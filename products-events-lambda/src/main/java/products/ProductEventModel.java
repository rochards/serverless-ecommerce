package products;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@ToString
public class ProductEventModel {

    private String code;
    private String eventTypeAndTimestamp;
    private String email;
    private String requestId;
    private EventType eventType;
    private Info info;
    private long createdAt;
    private long ttl;

    public ProductEventModel() {
    }

    public ProductEventModel(ProductEventRequest eventRequest) {
        var instant = Instant.now();

        this.code = String.format("#product_%s", eventRequest.getProductCode());
        this.eventTypeAndTimestamp = String.format("%s#%d", eventRequest.getEventType(), instant.toEpochMilli());
        this.email = eventRequest.getEmail();
        this.requestId = eventRequest.getRequestId();
        this.eventType = eventRequest.getEventType();
        this.info = new Info(eventRequest.getProductId(), eventRequest.getProductPrice());
        this.createdAt = instant.toEpochMilli();
        this.ttl = instant.plus(Duration.ofMinutes(5)).getEpochSecond();
    }

    @Getter
    static class Info {
        private String productId;
        private BigDecimal productPrice;

        public Info(String productId, BigDecimal productPrice) {
            this.productId = productId;
            this.productPrice = productPrice;
        }
    }
}
