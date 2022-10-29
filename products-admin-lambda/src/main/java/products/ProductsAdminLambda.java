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

public class ProductsAdminLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(ProductsAdminLambda.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        LOGGER.log(Level.INFO, "API Gateway RequestId: {}", input.getRequestContext().getRequestId());

        if (input.getResource().equals("/products/{id}")) {
            var productId = input.getPathParameters().get("id");
            if (input.getHttpMethod().equals("PUT")) {
                LOGGER.log(Level.INFO, "PUT /products/{}", productId);
                return buildResponse("Put products ok");
            } else if (input.getHttpMethod().equals("DELETE")) {
                LOGGER.log(Level.INFO, "DELETE /products/{}", productId);
                return buildResponse("Delete products ok");
            }
        }
        // chamada em /products
        return buildResponse("Post products Ok");
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
