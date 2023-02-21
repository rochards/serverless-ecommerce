package com.rochards.orders.event;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderEventPublisher {

    private static final Logger LOGGER = LogManager.getLogger(OrderEventPublisher.class);
    private static final String ORDERS_TOPIC_ARN;

    static {
        ORDERS_TOPIC_ARN = System.getenv("ORDERS_TOPIC_ARN");
    }

    private final Gson gson;
    private final AmazonSNS amazonSNS;


    public OrderEventPublisher() {
        // considerar utilizar injecao de dependencias para diminuir o acoplamento
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.amazonSNS = AmazonSNSClientBuilder.standard().build();
    }

    public void publishOrderEvent(OrderTopic orderTopic) {
        var orderTopicJson = gson.toJson(orderTopic);
        LOGGER.info("Publishing event = {} \nin topic ARN = {}", orderTopicJson, ORDERS_TOPIC_ARN);
        PublishResult publish = amazonSNS.publish(ORDERS_TOPIC_ARN, gson.toJson(orderTopic));
        LOGGER.info("Received response for lambdaRequestId = {}: MessageId = {}",
                orderTopic.getOrderEvent().getLambdaRequestId(), publish.getMessageId());
    }
}
