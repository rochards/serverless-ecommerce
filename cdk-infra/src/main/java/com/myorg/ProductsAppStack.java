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

    private final Table productsDdbTable;
    private final Function productsFetchHandler;
    private final Function productsAdminHandler;

    public ProductsAppStack(Construct scope, String stackId) {
        super(scope, stackId, null);

        this.productsDdbTable = createDynamoDBTable();
        this.productsFetchHandler = createProductsFetchLambda();
        this.productsAdminHandler = createProductsAdminLambda();

        // atribuindo as lambdas permissões de leitura e escrita na tabela
        this.productsDdbTable.grantReadData(this.productsFetchHandler);
        this.productsDdbTable.grantReadWriteData(this.productsAdminHandler);
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

    private Function createProductsFetchLambda() {
        String lambdaName = "ProductsFetchLambda";

        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .handler("products.ProductsFetchLambda") /* igual ao do projeto products-lambda. É permitido referenciar
                                                            o pacote.nome_da_classe pq esta implementa a interface RequestHandler */
                .memorySize(512)
                .timeout(Duration.seconds(5))
                .code(Code.fromAsset("lambdas/products/products-lambda-1.4-SNAPSHOT.jar"))
                .runtime(Runtime.JAVA_11)
                .logRetention(RetentionDays.ONE_DAY)
                .tracing(Tracing.ACTIVE) // ativando o X-Ray
//                .insightsVersion(LambdaInsightsVersion.VERSION_1_0_135_0) comentado para reduzir gastos na conta
                .build();
    }

    private Function createProductsAdminLambda() {
        String lambdaName = "ProductsAdminLambda";

        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .handler("products.ProductsAdminLambda")
                .memorySize(512)
                .timeout(Duration.seconds(5))
                .code(Code.fromAsset("lambdas/products/products-admin-lambda-1.10-SNAPSHOT.jar"))
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
