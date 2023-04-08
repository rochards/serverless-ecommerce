package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class OrdersEventsFetch implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(OrdersEventsFetch.class);
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LOGGER.info("apiGatewayProxyRequestEvent: {}", apiGatewayProxyRequestEvent);
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(204);
        response.setHeaders(Map.of("Content-Type", "application/json"));
        return response;
    }
}
