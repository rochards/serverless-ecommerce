package orders;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APIGatewayParseRequest {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger(APIGatewayParseRequest.class);

    public static OrderRequest parsePostRequest(APIGatewayProxyRequestEvent input) {
        var inputBody = input.getBody();
        LOGGER.info("POST - Received inputBody = {}", inputBody);
        return GSON.fromJson(inputBody, OrderRequest.class);
    }
}
