package integration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import products.Product;

public class PublishProductEvents {

    private static final Logger LOGGER = LogManager.getLogger(PublishProductEvents.class);
    private final LambdaEventsAsyncClient client = new LambdaEventsAsyncClient();

    public void publishAsyncEvent(Product product, ProductEvent.EventType eventType,
                                         String senderEmail,
                                         String lambdaRequestId) {
        var productEvent = ProductEvent.builder()
                .requestId(lambdaRequestId)
                .email(senderEmail)
                .eventType(eventType)
                .productCode(product.getCode())
                .productPrice(product.getPrice())
                .productId(product.getId())
                .build();

        LOGGER.info("Invoking async request with event: {}", productEvent);

        client.sendEvent(productEvent);

        LOGGER.info("Async request sent");
    }
}
