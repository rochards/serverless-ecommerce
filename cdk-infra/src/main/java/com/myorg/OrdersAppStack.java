package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.LambdaSubscription;
import software.constructs.Construct;

import java.util.*;

public class OrdersAppStack extends Stack {

    private final Function ordersHandler;

    public OrdersAppStack(Construct scope, String stackId, Table productsDdbTable, Table eventsDdbTable) {
        super(scope, stackId, null);

        Table ordersDdbTable = createOrdersDdbTable();
        Topic ordersTopic = createOrdersTopic();

        ordersHandler = createLambda("OrdersLambda", "com.rochards.orders.OrdersLambda",
                "lambdas/orders/orders-lambda-2.1-SNAPSHOT.jar");
        ordersHandler.addEnvironment("PRODUCTS_TABLE_NAME", productsDdbTable.getTableName());
        ordersHandler.addEnvironment("ORDERS_TABLE_NAME", ordersDdbTable.getTableName());
        ordersHandler.addEnvironment("ORDERS_TOPIC_ARN", ordersTopic.getTopicArn());

        Function orderEventsHandler = createLambda("OrderEventsLambda", "com.rochards.orders.OrderEventsLambda",
                "lambdas/orders/order-events-lambda-1.1-SNAPSHOT.jar");
        orderEventsHandler.addEnvironment("EVENTS_TABLE_NAME", eventsDdbTable.getTableName());
        orderEventsHandler.addToRolePolicy(
                /* exemplo de como ser específico para as operacoes no DynamoDB. Há tbm a possibilidade de vc colocar
                * conditions, mas fica muito trabalhoso em java.
                * */
                PolicyStatement.Builder.create()
                        .effect(Effect.ALLOW)
                        .actions(Arrays.asList("dynamodb:PutItem", "dynamodb:BatchWriteItem"))
                        .resources(Collections.singletonList(eventsDdbTable.getTableArn()))
                        .build()
        );


        // atribuindo permissoes para a lambda nas tabelas
        productsDdbTable.grantReadData(ordersHandler);
        ordersDdbTable.grantReadWriteData(ordersHandler);

        // atribuindo permissoes para as lambdas no topico
        ordersTopic.grantPublish(ordersHandler);
        ordersTopic.addSubscription(new LambdaSubscription(orderEventsHandler));
    }

    private Table createOrdersDdbTable() {
        String tableName = "OrdersTable";
        return Table.Builder.create(this, tableName)
                .tableName(tableName)
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(3)
                .writeCapacity(3)
                .removalPolicy(RemovalPolicy.DESTROY)
                .partitionKey(
                        Attribute.builder()
                                .name("Email")
                                .type(AttributeType.STRING)
                                .build())
                .sortKey(
                        Attribute.builder()
                                .name("OrderId")
                                .type(AttributeType.STRING)
                                .build())
                .build();
    }

    private Topic createOrdersTopic() {
        return Topic.Builder.create(this, "OrdersTopic")
                .topicName("OrdersTopic")
                .build();
    }

    private Function createLambda(String lambdaName, String handlerName, String pathToJar) {
        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .handler(handlerName) // É permitido referenciar o pacote.nome_da_classe se implementar a interface
                                      // RequestHandler
                .memorySize(512)
                .timeout(Duration.seconds(10))
                .code(Code.fromAsset(pathToJar))
                .runtime(Runtime.JAVA_11)
                .logRetention(RetentionDays.ONE_DAY)
                .tracing(Tracing.ACTIVE)
                // .insightsVersion(LambdaInsightsVersion.VERSION_1_0_135_0) comentado para
                // reduzir gastos na conta
                .build();
    }

    public Function getOrdersHandler() {
        return ordersHandler;
    }
}
