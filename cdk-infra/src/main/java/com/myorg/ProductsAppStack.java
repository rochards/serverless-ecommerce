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

public class ProductsAppStack extends Stack {

    private final Function productsFetchHandler;
    private final Function productsAdminHandler;

    public ProductsAppStack(Construct scope, String stackId, Table eventsTable) {
        super(scope, stackId, null);

        Function productEventsHandler = createLambda("ProductsEventsLambda", "products.ProductsEventsLambda",
                "lambdas/products/products-events-lambda-1.0-SNAPSHOT.jar");
        eventsTable.grantWriteData(productEventsHandler);

        this.productsAdminHandler = createLambda("ProductsAdminLambda", "products.ProductsAdminLambda",
                "lambdas/products/products-admin-lambda-1.16-SNAPSHOT.jar");
        this.productsAdminHandler.addEnvironment("PRODUCTS_EVENTS_FUNCTION_NAME", productEventsHandler.getFunctionName());
        productEventsHandler.grantInvoke(this.productsAdminHandler); // atribuindo permissao para a admin invocar a events

        this.productsFetchHandler = createLambda("ProductsFetchLambda", "products.ProductsFetchLambda",
                "lambdas/products/products-lambda-1.5-SNAPSHOT.jar");

        Table productsDdbTable = createDynamoDBTable();
        // atribuindo as lambdas permissões de leitura e escrita na tabela
        productsDdbTable.grantReadWriteData(this.productsAdminHandler);
        productsDdbTable.grantReadData(this.productsFetchHandler);
    }

    private Table createDynamoDBTable() {
        String tableName = "ProductsTable";
        return Table.Builder.create(this, tableName)
                .tableName(tableName)
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(3)
                .writeCapacity(3)
                .removalPolicy(RemovalPolicy.DESTROY)
                .partitionKey(
                        Attribute.builder()
                                .name("Id")
                                .type(AttributeType.STRING)
                                .build()
                )
                .build();
    }

    private Function createLambda(String lambdaName, String handlerName, String pathToJar) {
        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .handler(handlerName)  // É permitido referenciar o pacote.nome_da_classe se implementar a interface RequestHandler
                .memorySize(512)
                .timeout(Duration.seconds(5))
                .code(Code.fromAsset(pathToJar))
                .runtime(Runtime.JAVA_11)
                .logRetention(RetentionDays.ONE_DAY)
                .tracing(Tracing.ACTIVE)
//                .insightsVersion(LambdaInsightsVersion.VERSION_1_0_135_0) comentado para reduzir gastos na conta
                .build();
    }

    public Function getProductsFetchHandler() {
        return productsFetchHandler;
    }

    public Function getProductsAdminHandler() {
        return productsAdminHandler;
    }
}
