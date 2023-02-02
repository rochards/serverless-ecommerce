package orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import product.ProductRepository;

import java.util.Objects;

public class OrdersLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LogManager.getLogger(OrdersLambda.class);

    private final OrderRepository orderRepository = new OrderRepository();
    private final ProductRepository productRepository = new ProductRepository();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        var method = input.getHttpMethod();
        var apiGtwRequestId = input.getRequestContext().getRequestId();
        var lambdaRequestId = context.getAwsRequestId();


        LOGGER.info("API Gateway RequestId = {} - Lambda RequestId = {}", apiGtwRequestId, lambdaRequestId);

        switch (method) {
            case "GET":
                handleGetOrder(input);
                break;

            case "POST":
                handlePostOrder(input);
                break;

            case "DELETE":
                handleDeleteOrder(input);
                break;

            default:
                LOGGER.error("Method = {} not allowed", method);
        }

        return null;
    }

    private void handleGetOrder(APIGatewayProxyRequestEvent input) {
        var email = input.getQueryStringParameters().get("email");
        var orderId = input.getQueryStringParameters().get("orderId");
        LOGGER.info("GET /orders?email={}&orderId={}", email, orderId);

        if (Objects.nonNull(orderId)) {
            // tratar busca /orders?email={email}&orderId={order_Id}
        } else {
            // tratar busca /orders?email={email}
        }
    }

    private void handlePostOrder(APIGatewayProxyRequestEvent input) {
        LOGGER.info("POST /orders");
    }

    private void handleDeleteOrder(APIGatewayProxyRequestEvent input) {
        var email = input.getQueryStringParameters().get("email");
        var orderId = input.getQueryStringParameters().get("orderId");
        LOGGER.info("DELETE /orders?email={}&orderId={}", email, orderId);
    }
}
