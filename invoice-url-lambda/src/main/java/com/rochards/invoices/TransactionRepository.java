package com.rochards.invoices;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransactionRepository {

    private static final Logger LOGGER = LogManager.getLogger(TransactionRepository.class);

    private final DynamoDBMapper mapper;

    public TransactionRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public void save(TransactionModel transaction) {
        LOGGER.info("Saving transaction: {}", transaction);
        mapper.save(transaction);
        LOGGER.info("Transaction saved: {}", transaction);
    }
}
