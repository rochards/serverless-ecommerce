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

    private final ProductRepository repository = new ProductRepository();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        LOGGER.log(Level.INFO, "API Gateway RequestEvent: {}", input);

        if (input.getResource().equals("/products/{id}")) {
            var productId = input.getPathParameters().get("id");
            if (input.getHttpMethod().equals("PUT")) {
                return handleUpdateProduct(input, productId);
            } else if (input.getHttpMethod().equals("DELETE")) {
                LOGGER.log(Level.INFO, "DELETE /products/{}", productId);
                return buildResponse("Delete products ok");
            }
        }
        // chamada em POST /products
        return handleSaveProduct(input);
    }

    private APIGatewayProxyResponseEvent handleSaveProduct(APIGatewayProxyRequestEvent input) {
        var inputBody = input.getBody();
        LOGGER.log(Level.INFO, "POST - Saving received product: {}", inputBody);

        var product = GSON.fromJson(inputBody, Product.class);
        product = repository.save(product);

        return APIGatewayResponse.created201(product);
    }

    private APIGatewayProxyResponseEvent handleUpdateProduct(APIGatewayProxyRequestEvent input, String productId) {
        String inputBody = input.getBody();
        LOGGER.log(Level.INFO, "PUT - Updating received product: {}. Received product id: {}", inputBody, productId);

        var updatedProduct = GSON.fromJson(inputBody, Product.class);
        var optionalProduct = repository.update(productId, updatedProduct);

        return optionalProduct.map(APIGatewayResponse::ok200)
                .orElseGet(() -> APIGatewayResponse.notFound404(String.format("Not found product with id %s to be updated", productId)));
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
