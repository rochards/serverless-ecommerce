package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class APIGatewayParseResponse {

    private APIGatewayParseResponse() {
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, String> HEADERS = Map.of("Content-Type", "application/json");

    public static APIGatewayProxyResponseEvent ok200(Object orderResponse) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(orderResponse));

        return response;
    }

    public static APIGatewayProxyResponseEvent created201(OrderResponse orderResponse) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(201);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(orderResponse));

        return response;
    }

    public static APIGatewayProxyResponseEvent badRequest400(String message) {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(Map.of("message", message)));

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

    public static APIGatewayProxyResponseEvent methodNotAllowed405() {
        var response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(405);
        response.setIsBase64Encoded(false);
        response.setHeaders(HEADERS);
        response.setBody(GSON.toJson(Map.of("message", "Method Not Allowed")));

        return response;
    }
}
