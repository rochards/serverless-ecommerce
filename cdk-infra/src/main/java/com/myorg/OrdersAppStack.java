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
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.sns.StringConditions;
import software.amazon.awscdk.services.sns.SubscriptionFilter;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.LambdaSubscription;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

import java.util.*;

public class OrdersAppStack extends Stack {

    private final Function ordersHandler;
    private final Queue ordersQueue;

    public OrdersAppStack(Construct scope, String stackId, Table productsDdbTable, Table eventsDdbTable) {
        super(scope, stackId, null);

        Table ordersDdbTable = createOrdersDdbTable();
        Topic ordersTopic = createOrdersTopic();

        ordersQueue = createOrdersQueue();
        addOrdersQueueToOrdersTopic(ordersTopic);

        ordersHandler = createLambda("OrdersLambda", "com.rochards.orders.OrdersLambda",
                "lambdas/orders/orders-lambda-2.2-SNAPSHOT.jar");
        ordersHandler.addEnvironment("PRODUCTS_TABLE_NAME", productsDdbTable.getTableName());
        ordersHandler.addEnvironment("ORDERS_TABLE_NAME", ordersDdbTable.getTableName());
        ordersHandler.addEnvironment("ORDERS_TOPIC_ARN", ordersTopic.getTopicArn());
        // atribuindo permissoes para a lambda nas tabelas
        productsDdbTable.grantReadData(ordersHandler);
        ordersDdbTable.grantReadWriteData(ordersHandler);
        // atribuindo permissoes para as lambdas no topico
        ordersTopic.grantPublish(ordersHandler);


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
        ordersTopic.addSubscription(new LambdaSubscription(orderEventsHandler));


        Function orderEmailsHandler = createLambda("OrderEmailsLambda", "com.rochards.orders.OrderEmailsLambda",
                "lambdas/orders/order-emails-lambda-1.0-SNAPSHOT.jar");
        configureSqsInvocationForOrderEmailsHandler(orderEmailsHandler);
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

    private Queue createOrdersQueue() {
        return Queue.Builder.create(this, "OrdersQueue")
                .queueName("OrdersQueue")
                .build();
    }

    private void addOrdersQueueToOrdersTopic(Topic ordersTopic) {
        /*
         * Criando filtro para o SNS enviar as mensagens para o SQS baseado no valor do eventType. Tanto o parametro "eventType"
         * quanto seus possiveis valor foi definido na classe OrderEventPublisher do projeto orders-lambda
         * */
        Map<String, SubscriptionFilter> filterPolicy = new HashMap<>();
        filterPolicy.put("eventType", SubscriptionFilter.stringFilter(
                StringConditions.builder()
                        .allowlist(Collections.singletonList("ORDER_CREATED"))
                        .build())
        );
        ordersTopic.addSubscription(
                SqsSubscription.Builder.create(ordersQueue)
                        .filterPolicy(filterPolicy)
                        .build()
        );
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

    private void configureSqsInvocationForOrderEmailsHandler(Function orderEmailsHandler) {
        orderEmailsHandler.addEventSource(
                SqsEventSource.Builder.create(ordersQueue)
                        .batchSize(5) // quantidade de eventos que a Lambda vai pegar da fila
                        .maxBatchingWindow(Duration.seconds(5)) /* significa q se der 5 segundos, independente do numero de mensagens,
                                                                        a AWS Lambda vai infocar minha funcao */
                        .build()
        );
        ordersQueue.grantConsumeMessages(orderEmailsHandler);
    }

    public Function getOrdersHandler() {
        return ordersHandler;
    }
}
