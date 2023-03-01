package com.rochards.orders;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

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
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "EventType")
    private EventType eventType;
    @DynamoDBAttribute(attributeName = "Info")
    private Info info;
    @DynamoDBAttribute(attributeName = "Ttl")
    private long ttl;

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
    private static class Info {
        @DynamoDBAttribute(attributeName = "OrderId")
        private String orderId;
        @DynamoDBAttribute(attributeName = "ProductCodes")
        private List<String> productCodes;
        @DynamoDBAttribute(attributeName = "MessageId")
        private String messageId;

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Info{" +
                    "orderId='" + orderId + '\'' +
                    ", productCodes=" + productCodes +
                    ", messageId='" + messageId + '\'' +
                    '}';
        }
    }
}
