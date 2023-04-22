package com.rochards.orders;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersEventsRepository {

    private static final Logger LOGGER = LogManager.getLogger(OrdersEventsRepository.class);
    private final DynamoDBMapper mapper;

    public OrdersEventsRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

//    public Optional<OrdersEventsModel> findByEmailAndEventType(String email, String eventType) {
//        var queryExpression = new DynamoDBQueryExpression<OrdersEventsModel>()
//                .withIndexName(OrdersEventsModel.GSI_NAME)
//                .withKeyConditionExpression(
//                        String.format("%s = :v_email and begins_with(%s, :v_eventType)", OrdersEventsModel.HASH_KEY_GSI_ATTRIBUTE_NAME, OrdersEventsModel.RANGE_KEY_GSI_ATTRIBUTE_NAME)
//                )
//                .withExpressionAttributeValues(Map.ofEntries(
//                        Map.entry(":v_email", new AttributeValue().withS(email)),
//                        Map.entry(":v_eventType", new AttributeValue().withS(eventType))
//                ));
//
//        return null;
//    }

    public List<OrderEventModel> findByEmail(String email) {
        var queryExpression = new DynamoDBQueryExpression<OrderEventModel>()
                .withIndexName(OrderEventModel.GSI_NAME)
                .withConsistentRead(false) // sou obrigado a informar por estar consulta em um GSI
                .withKeyConditionExpression(
                        String.format("%s = :v_email", OrderEventModel.HASH_KEY_GSI_ATTRIBUTE_NAME)
                )
                .withExpressionAttributeValues(Map.ofEntries(
                        Map.entry(":v_email", new AttributeValue().withS(email))
                ));

        LOGGER.info("Finding events for email: {}", email);

        PaginatedQueryList<OrderEventModel> events = mapper.query(OrderEventModel.class, queryExpression);

        LOGGER.info("Email: {}, found count: {}", email, events.size());
        return new ArrayList<>(events);
    }

    /*
    * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.client.QueryGSI.java-sdk-v1.html
    * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html#Query.KeyConditionExpressions
    * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GSI.html
    * */
}