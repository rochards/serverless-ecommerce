package com.rochards.orders;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName = "EventsTable")
public class OrderEventModel {

    @DynamoDBIgnore
    public static final String HASH_KEY_GSI_ATTRIBUTE_NAME = "Email";
    @DynamoDBIgnore
    public static final String RANGE_KEY_GSI_ATTRIBUTE_NAME = "EventTypeAndTimestamp";
    @DynamoDBIgnore
    public static final String GSI_NAME = "EmailAndEventTypeIndex";

    @DynamoDBIndexHashKey(globalSecondaryIndexName = GSI_NAME, attributeName = HASH_KEY_GSI_ATTRIBUTE_NAME)
    private String email;
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = GSI_NAME, attributeName = RANGE_KEY_GSI_ATTRIBUTE_NAME)
    private String eventTypeAndTimestamp;

    @DynamoDBHashKey(attributeName = "Code") // msm no GSI sou obrigado a informar
    private String code;

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "EventType")
    private EventType eventType;

    @DynamoDBAttribute(attributeName = "CreatedAt")
    private long createdAt;

    @DynamoDBAttribute(attributeName = "RequestId")
    private String requestId;
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
    @NoArgsConstructor // nao esquecer, ja que informou o AllArgsConstructor
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