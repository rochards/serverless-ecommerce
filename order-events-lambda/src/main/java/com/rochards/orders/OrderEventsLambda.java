package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.rochards.orders.model.OrderEventModel;
import com.rochards.orders.model.OrderEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class OrderEventsLambda implements RequestHandler<SNSEvent, Void> {

    private static final Logger LOGGER = LogManager.getLogger(OrderEventsLambda.class);
    private final OrderEventRepository repository = new OrderEventRepository();
    @Override
    public Void handleRequest(SNSEvent snsEvent, Context context) {
        LOGGER.info("Received SNSEvent: {}", snsEvent);
        var orderTopics = SNSEventParserRequest.parseSNSInvocation(snsEvent);
        var orders = orderTopics.stream()
                .map(OrderEventModel::new)
                .collect(Collectors.toList());

        repository.batchSave(orders);
        return null;
    }
}
