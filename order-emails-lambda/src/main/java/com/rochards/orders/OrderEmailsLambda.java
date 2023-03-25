package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderEmailsLambda implements RequestHandler<SQSEvent, Void> {

    private static final Logger LOGGER = LogManager.getLogger(OrderEmailsLambda.class);
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        sqsEvent.getRecords()
                .forEach(sqsMessage -> LOGGER.info("SQSRecord: {}", sqsMessage));
        return null;
    }
}
