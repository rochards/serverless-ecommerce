package products;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class APIGatewayResponse {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> HEADERS = Map.of("Content-Type", "application/json");

    public static APIGatewayProxyResponseEvent response200(Product product) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(product));

        return response;
    }

    public static APIGatewayProxyResponseEvent notFound404(String message) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(404);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(Map.of("message", message)));

        return response;
    }
}
