package orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import product.ProductModel;
import product.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                return handleGetOrder(input);
            case "POST":
                return handlePostOrder(input);
            case "DELETE":
                return handleDeleteOrder(input);
            default:
                LOGGER.error("Method = {} not allowed", method);
                return APIGatewayParseResponse.methodNotAllowed405();
        }
    }

    private APIGatewayProxyResponseEvent handleGetOrder(APIGatewayProxyRequestEvent input) {
        var email = input.getQueryStringParameters().get("email");
        var orderId = input.getQueryStringParameters().get("orderId");

        LOGGER.info("GET /orders?email={}&orderId={}", email, orderId);
        if (Objects.nonNull(orderId)) {
            var optOrderModel = orderRepository.findByEmailAndOrderId(email, orderId);
            return optOrderModel.map(orderModel -> {
                        var orderResponse = OrderParse.modelToResponse(orderModel);
                        LOGGER.info("Sending response to client. OrderResponse = {}", orderResponse);
                        return APIGatewayParseResponse.ok200(orderResponse);
                    })
                    .orElseGet(() -> APIGatewayParseResponse.notFound404(String.format("Not found orderId = %s for email = %s", orderId, email)));
        } else {
            var ordersResponse = orderRepository.findByEmail(email)
                    .stream()
                    .map(OrderParse::modelToResponse)
                    .collect(Collectors.toList());

            if (!ordersResponse.isEmpty()) {
                LOGGER.info("Sending response to client. OrderResponseList = {}", ordersResponse);
                return APIGatewayParseResponse.ok200(ordersResponse);
            }

            return APIGatewayParseResponse.notFound404("Not found order for email = " + email);
        }
    }

    private APIGatewayProxyResponseEvent handlePostOrder(APIGatewayProxyRequestEvent input) {
        LOGGER.info("POST /orders");
        var orderRequest = APIGatewayParseRequest.parsePostRequest(input);

        List<ProductModel> foundProducts = productRepository.findByIds(orderRequest.getProductsIds());
        if (foundProducts.size() != orderRequest.getProductsIds().size()) {
            return APIGatewayParseResponse.badRequest400("Some orderRequest.productIds were not found in database");
        }

        var orderToSave = OrderParse.requestToModel(orderRequest, foundProducts);
        orderRepository.save(orderToSave);

        var orderResponse = OrderParse.modelToResponse(orderToSave);

        LOGGER.info("Sending order response to client. OrderResponse = {}", orderResponse);
        return APIGatewayParseResponse.created201(orderResponse);
    }

    private APIGatewayProxyResponseEvent handleDeleteOrder(APIGatewayProxyRequestEvent input) {
        var email = input.getQueryStringParameters().get("email");
        var orderId = input.getQueryStringParameters().get("orderId");
        LOGGER.info("DELETE /orders?email={}&orderId={}", email, orderId);

        return null;
    }
}
