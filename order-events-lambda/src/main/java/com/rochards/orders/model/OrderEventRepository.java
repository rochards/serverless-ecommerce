package com.rochards.orders.model;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OrderEventRepository {
    private static final Logger LOGGER = LogManager.getLogger(OrderEventRepository.class);
    private final DynamoDBMapper mapper;

    public OrderEventRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public void batchSave(List<OrderEventModel> orderEvents) {
        LOGGER.info("Saving batch OrderEventModel in DynamoDB: {}", orderEvents);
        mapper.batchSave(orderEvents);
        LOGGER.info("Each OrderEventModel saved with success");
    }
}
