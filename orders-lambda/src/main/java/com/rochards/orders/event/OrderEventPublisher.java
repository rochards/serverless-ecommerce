package com.rochards.orders.event;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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
        /*
        * Uma forma mais simples de fazer a publicação do evento seria:
        * -> amazonSNS.publish(ORDERS_TOPIC_ARN, gson.toJson(orderTopic))
        * A forma abaixo nos da mais flexibilidade. Estou enviando um eventType para eu posso configurar o SNS para invocar
        * a lambda para determinados eventType, economizando em invocacoes. No momento de inserir a Lambda no topico, vc
        * add tbm a filterPolicy.
        * */
        var messageAttributes = Map.of(
                "eventType", new MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue(orderTopic.getType().name())
        );
        var request = new PublishRequest()
                .withTopicArn(ORDERS_TOPIC_ARN)
                .withMessage(orderTopicJson)
                .withMessageAttributes(messageAttributes);

        LOGGER.info("Publishing event = {} \nin topic ARN = {}", orderTopicJson, ORDERS_TOPIC_ARN);

        var publish = amazonSNS.publish(request);

        LOGGER.info("Received response for lambdaRequestId = {}: MessageId = {}",
                orderTopic.getOrderEvent().getLambdaRequestId(), publish.getMessageId());
    }
}
