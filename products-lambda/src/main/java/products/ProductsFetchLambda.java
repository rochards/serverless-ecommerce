package products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ProductsFetchLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(ProductsFetchLambda.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        LOGGER.log(Level.INFO, "API Gateway RequestId: {}", input.getRequestContext().getRequestId());

        return buildResponse();
    }

    private APIGatewayProxyResponseEvent buildResponse() {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setIsBase64Encoded(false);
        response.setHeaders(
                Map.of("Content-Type", "application/json")
        );
        response.setBody(GSON.toJson(Map.of("message", "Get products Ok")));

        return response;
    }
}
