package products;

import com.amazonaws.services.lambda.runtime.Context;
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

    private final ProductRepository repository = new ProductRepository();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        LOGGER.log(Level.INFO, "API Gateway RequestId: {}", input.getRequestContext().getRequestId());

        if (input.getResource().equals("/products/{id}")) {
            String id = input.getPathParameters().get("id");
            LOGGER.log(Level.INFO, "GET /products/{}", id);

            return repository.findById(id)
                    .map(APIGatewayResponse::response200)
                    .orElseGet(() -> APIGatewayResponse.notFound404("Not found product with id: " + id));
        }

        return buildResponse("Get products Ok");
    }

    private APIGatewayProxyResponseEvent buildResponse(String message) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setIsBase64Encoded(false);
        response.setHeaders(
                Map.of("Content-Type", "application/json")
        );
        response.setBody(GSON.toJson(Map.of("message", message)));

        return response;
    }
}
