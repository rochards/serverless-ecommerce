package orders;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderRepository {

    private static final Logger LOGGER = LogManager.getLogger(OrderRepository.class);
    private final DynamoDBMapper mapper;

    public OrderRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public OrderModel save(OrderModel order) {
        mapper.save(order);
        return order;
    }
}
