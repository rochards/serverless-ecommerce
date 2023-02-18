package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

public class OrdersAppStack extends Stack {

    private final Function ordersHandler;

    public OrdersAppStack(Construct scope, String stackId, Table productsDdbTable) {
        super(scope, stackId, null);

        Table ordersDdbTable = createOrdersDdbTable();

        ordersHandler = createLambda("OrdersLambda", "orders.OrdersLambda",
                "lambdas/orders/orders-lambda-1.2-SNAPSHOT.jar");
        ordersHandler.addEnvironment("PRODUCTS_TABLE_NAME", productsDdbTable.getTableName());
        ordersHandler.addEnvironment("ORDERS_TABLE_NAME", ordersDdbTable.getTableName());

        // atribuindo permissoes para a lambda nas tabelas
        productsDdbTable.grantReadData(ordersHandler);
        ordersDdbTable.grantReadWriteData(ordersHandler);
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
