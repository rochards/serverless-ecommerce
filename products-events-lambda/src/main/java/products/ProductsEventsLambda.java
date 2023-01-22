package products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProductsEventsLambda implements RequestHandler<ProductEventRequest, Void> {

    private static final Logger LOGGER = LogManager.getLogger(ProductsEventsLambda.class);

    private final ProductEventRepository repository = new ProductEventRepository();

    @Override
    public Void handleRequest(ProductEventRequest input, Context context) {
        LOGGER.info("Received ProductEventRequest: {}", input);

        var productEvent = new ProductEventModel(input);
        repository.save(productEvent);
        return null;
    }
}
