package products;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Setter
@Getter
@DynamoDBTable(tableName = "EventsTable") // nome conforme definido no projeto do cdk
public class ProductEventModel {

    @DynamoDBHashKey(attributeName = "Code") // nome conforme definido no projeto do cdk
    private String code;

    @DynamoDBRangeKey(attributeName = "EventTypeAndTimestamp") // nome conforme definido no projeto do cdk
    private String eventTypeAndTimestamp;

    @DynamoDBAttribute(attributeName = "Email")
    private String email;

    @DynamoDBAttribute(attributeName = "RequestId")
    private String requestId;

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "EventType")
    private EventType eventType;

    @DynamoDBAttribute(attributeName = "Info")
    private Info info;

    @DynamoDBAttribute(attributeName = "CreatedAt")
    private long createdAt;

    @DynamoDBAttribute(attributeName = "Ttl") // nome conforme definido no projeto do cdk
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

    @DynamoDBIgnore
    @Override
    public String toString() {
        return "ProductEventModel{" +
                "code='" + code + '\'' +
                ", eventTypeAndTimestamp='" + eventTypeAndTimestamp + '\'' +
                ", email='" + email + '\'' +
                ", requestId='" + requestId + '\'' +
                ", eventType=" + eventType +
                ", info=" + info +
                ", createdAt=" + createdAt +
                ", ttl=" + ttl +
                '}';
    }

    @Getter
    @Setter
    @DynamoDBDocument
    public static class Info {

        @DynamoDBAttribute(attributeName = "ProductId")
        private String productId;

        @DynamoDBAttribute(attributeName = "ProductPrice")
        private BigDecimal productPrice;

        // importante ter um construtor vazio para o mapeamento do dynamodb
        public Info() {
        }

        public Info(String productId, BigDecimal productPrice) {
            this.productId = productId;
            this.productPrice = productPrice;
        }

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Info{" +
                    "productId='" + productId + '\'' +
                    ", productPrice=" + productPrice +
                    '}';
        }
    }
}
