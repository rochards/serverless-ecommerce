package products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProductsEventsLambda implements RequestHandler<ProductEventRequest, Void> {

    private static final Logger LOGGER = LogManager.getLogger(ProductsEventsLambda.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    @Override
    public Void handleRequest(ProductEventRequest input, Context context) {
        LOGGER.log(Level.INFO, "Received ProductEventRequest: {}", input);
        var productEvent = new ProductEventModel(input);
        LOGGER.log(Level.INFO, "ProductEventModel: {}", productEvent);
        return null;
    }
}
