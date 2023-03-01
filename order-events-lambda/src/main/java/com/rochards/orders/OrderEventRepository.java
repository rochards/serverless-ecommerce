package com.rochards.orders;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderEventRepository {
    private static final Logger LOGGER = LogManager.getLogger(OrderEventRepository.class);
    private final DynamoDBMapper mapper;

    public OrderEventRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public void save(OrderEventModel orderEvent) {
        LOGGER.info("Saving OrderEventModel in DynamoDB: {}", orderEvent);
        mapper.save(orderEvent);
        LOGGER.info("OrderEventModel saved with success");
    }
}
