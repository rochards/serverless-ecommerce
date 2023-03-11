package com.rochards.orders.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.rochards.orders.EventType;
import com.rochards.orders.OrderTopic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName = "EventsTable")
public class OrderEventModel {

    @DynamoDBIgnore
    public static final String HASH_KEY_ATTRIBUTE_NAME = "Code"; // conforme definido em EventsDynamoDBStack
    @DynamoDBIgnore
    public static final String RANGE_KEY_ATTRIBUTE_NAME = "EventTypeAndTimestamp";

    @DynamoDBHashKey(attributeName = HASH_KEY_ATTRIBUTE_NAME)
    private String code;
    @DynamoDBRangeKey(attributeName = RANGE_KEY_ATTRIBUTE_NAME)
    private String eventTypeAndTimestamp;
    @DynamoDBAttribute(attributeName = "Email")
    private String email;
    @DynamoDBAttribute(attributeName = "CreatedAt")
    private long createdAt;

    @DynamoDBAttribute(attributeName = "RequestId")
    private String requestId;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "EventType")
    private EventType eventType;
    @DynamoDBAttribute(attributeName = "Info")
    private Info info;
    @DynamoDBAttribute(attributeName = "Ttl")
    private long ttl;

    public OrderEventModel(OrderTopic orderTopic) {
        var instant = Instant.now();
        var orderEvent = orderTopic.getOrderEvent();

        this.code = String.format("#order_%s", orderEvent.getOrderId());
        this.eventTypeAndTimestamp = String.format("%s#%d", orderTopic.getType(), instant.toEpochMilli());
        this.email = orderEvent.getEmail();
        this.requestId = orderEvent.getLambdaRequestId();
        this.eventType = orderTopic.getType();
        this.info = new Info(orderEvent.getOrderId(), orderEvent.getProductCodes());
        this.createdAt = instant.toEpochMilli();
        this.ttl = instant.plus(Duration.ofMinutes(5)).getEpochSecond();
    }

    @DynamoDBIgnore
    @Override
    public String toString() {
        return "OrderEventModel{" +
                "code='" + code + '\'' +
                ", eventTypeAndTimestamp='" + eventTypeAndTimestamp + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", eventType=" + eventType +
                ", info=" + info +
                ", ttl=" + ttl +
                '}';
    }

    @Getter
    @Setter
    @DynamoDBDocument
    @AllArgsConstructor
    public static class Info {
        @DynamoDBAttribute(attributeName = "OrderId")
        private String orderId;
        @DynamoDBAttribute(attributeName = "ProductCodes")
        private List<String> productCodes;

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Info{" +
                    "orderId='" + orderId + '\'' +
                    ", productCodes=" + productCodes +
                    '}';
        }
    }
}
