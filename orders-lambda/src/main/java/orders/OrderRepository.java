package orders;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<OrderModel> findByEmail(String email) {
        var queryExpression = new DynamoDBQueryExpression<OrderModel>()
                .withKeyConditionExpression(OrderModel.HASH_KEY_ATTRIBUTE_NAME + " = :email")
                .withExpressionAttributeValues(
                        Map.of(":email", new AttributeValue().withS(email))
                );
        LOGGER.info("Querying orders by email = {}", email);

        PaginatedQueryList<OrderModel> orders = mapper.query(OrderModel.class, queryExpression);

        LOGGER.info("Result items = {}", orders.size());
        return orders;
    }

    public Optional<OrderModel> findByEmailAndOrderId(String email, String orderId) {
        LOGGER.info("Querying order by email = {} and orderId = {}", email, orderId);

        Optional<OrderModel> optOrder = Optional.ofNullable(mapper.load(OrderModel.class, email, orderId));

        LOGGER.info("Found any? {}", optOrder.isPresent());
        return optOrder;
    }

    public Optional<OrderModel> deleteByEmailAndOrderId(String email, String orderId) {
        /* daria para deleter instanciando o objeto e setando apenas a rashkey e rangkey, mas como queremos retornar
        os atributos deletados, vamos pesquisar antes
        var orderToDelete = new OrderModel();
        orderToDelete.setEmail(email);
        orderToDelete.setOrderId(orderId); */

        var optionalOrder = findByEmailAndOrderId(email, orderId);

        optionalOrder.ifPresent(orderModel -> {
            LOGGER.info("Deleting order = {}", orderModel.toString());
            mapper.delete(orderModel);
            LOGGER.info("Order deleted. orderId = {}", orderId);
        });

        return optionalOrder;
    }
}
