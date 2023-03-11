package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SNSEventParserRequest {

    private SNSEventParserRequest() {}
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger(SNSEventParserRequest.class);

    public static List<OrderTopic> parseSNSInvocation(SNSEvent event) {
        LOGGER.info("Parsing received SNSEvent");
        return event.getRecords().stream()
                .map(snsRecord -> {
                    String message = snsRecord.getSNS().getMessage();
                    LOGGER.info("Received messageId: {} - record: {}", snsRecord.getSNS().getMessageId(), message);
                    return message;
                })
                .map(message -> GSON.fromJson(message, OrderTopic.class))
                .collect(Collectors.toList());
    }
}
