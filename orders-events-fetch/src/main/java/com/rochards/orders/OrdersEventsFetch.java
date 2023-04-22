package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrdersEventsFetch implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(OrdersEventsFetch.class);
    private final OrdersEventsRepository repository = new OrdersEventsRepository();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        var apiGtwRequestId = input.getRequestContext().getRequestId();
        var lambdaRequestId = context.getAwsRequestId();

        LOGGER.info("API Gateway RequestId = {} - Lambda RequestId = {}", apiGtwRequestId, lambdaRequestId);

        return handleGetEvents(input);
    }

    private APIGatewayProxyResponseEvent handleGetEvents(APIGatewayProxyRequestEvent input) {
        var email = input.getQueryStringParameters().get("email");

        // o ideal seria transformar o parametro eventType no enum EventType para validar o valor enviado
        var eventType = input.getQueryStringParameters().get("eventType");

        LOGGER.info("GET /orders?email={}&eventType={}", email, eventType);

        if (Objects.nonNull(eventType) && !eventType.isEmpty()) {
            List<OrderEventResponse> responses = repository.findByEmailAndEventType(email, eventType)
                    .stream()
                    .map(OrdersEventsParser::toResponse)
                    .collect(Collectors.toList());

            return APIGatewayParserResponse.ok200(responses);
        }

        List<OrderEventResponse> responses = repository.findByEmail(email)
                .stream()
                .map(OrdersEventsParser::toResponse)
                .collect(Collectors.toList());

        return APIGatewayParserResponse.ok200(responses);
    }
}
