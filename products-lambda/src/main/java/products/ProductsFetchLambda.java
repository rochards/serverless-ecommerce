package products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProductsFetchLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(ProductsFetchLambda.class);

    private final ProductRepository repository = new ProductRepository();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        LOGGER.log(Level.INFO, "API Gateway RequestEvent: {}", input);

        String id = input.getPathParameters().get("id");

        return repository.findById(id)
                .map(APIGatewayResponse::response200)
                .orElseGet(() -> APIGatewayResponse.notFound404("Not found product with id: " + id));
    }
}
