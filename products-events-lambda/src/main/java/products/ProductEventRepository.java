package products;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProductEventRepository {

    private static final Logger LOGGER = LogManager.getLogger(ProductEventRepository.class);

    private final DynamoDBMapper mapper;

    public ProductEventRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public ProductEventModel save(ProductEventModel productEventModel) {
        LOGGER.info("Saving ProductEventModel in DynamoDB: {}", productEventModel);

        mapper.save(productEventModel);

        LOGGER.info("ProductEventModel saved with success");
        return productEventModel;
    }

}
