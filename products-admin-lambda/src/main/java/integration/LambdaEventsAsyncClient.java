package integration;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Future;

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
        this.client = AWSLambdaAsyncClientBuilder.defaultClient();
        this.request = new InvokeRequest()
                .withFunctionName(productsEventsFunctionName)
                .withInvocationType(InvocationType.Event);
    }

    public void sendEvent(ProductEvent event) {
        var eventJson = GSON.toJson(event);
        request.withPayload(eventJson);

        LOGGER.info("Sending async request payload: {}\nto: {}", request.getPayload().toString(), productsEventsFunctionName);
        Future<InvokeResult> invokeResultFuture = client.invokeAsync(request);

        LOGGER.info("Async request sent. Is done? {}", invokeResultFuture.isDone());
    }
}
