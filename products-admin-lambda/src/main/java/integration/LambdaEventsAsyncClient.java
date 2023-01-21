package integration;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LambdaEventsAsyncClient {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger(LambdaEventsAsyncClient.class);
    private static final String productsEventsFunctionName;

    static {
        productsEventsFunctionName = System.getenv("PRODUCTS_EVENTS_FUNCTION_NAME");
    }

    private final AWSLambdaAsync client;
    private final InvokeRequest request;


    public LambdaEventsAsyncClient() {
        this.client = AWSLambdaAsyncClientBuilder.standard().build();
        this.request = new InvokeRequest();
        request.withFunctionName(productsEventsFunctionName);
    }

    public void sendEvent(ProductEvent event) {
        var eventJson = GSON.toJson(event);
        request.withPayload(eventJson);

        LOGGER.info("Sending async request payload: {}\nto: {}", eventJson, productsEventsFunctionName);
        client.invokeAsync(request);
        LOGGER.info("Async request sent");
    }
}
