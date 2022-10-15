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
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class ProductsAppStack extends Stack {

    private final Table productsDdbTable;
    private final Function productsFetchHandler;
    private final Function productsAdminHandler;

    public ProductsAppStack(Construct scope, String stackId) {
        super(scope, stackId, null);

        this.productsDdbTable = createDynamoDBTable();
        this.productsFetchHandler = createProductsFetchLambda(this.productsDdbTable.getTableName());
        this.productsAdminHandler = createProductsAdminLambda(this.productsDdbTable.getTableName());

        // atribuindo as lambdas permissões de leitura e escrita na tabela
        this.productsDdbTable.grantReadData(this.productsFetchHandler);
        this.productsDdbTable.grantWriteData(this.productsAdminHandler);
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

    private Function createProductsFetchLambda(String tableName) {
        String lambdaName = "ProductsFetchLambda";
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("PRODUCTS_DDB_TABLE", tableName);

        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .handler("products.ProductsFetchLambda") /* igual ao do projeto products-lambda. É permitido referenciar
                                                            o pacote.nome_da_classe pq esta implementa a interface RequestHandler */
                .memorySize(512)
                .timeout(Duration.seconds(3))
                .code(Code.fromAsset("lambdas/products/products-lambda-1.0-SNAPSHOT.jar"))
                .runtime(Runtime.JAVA_11)
                .environment(envVariables)
                .build();
    }

    private Function createProductsAdminLambda(String tableName) {
        String lambdaName = "ProductsAdminLambda";
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("PRODUCTS_DDB_TABLE", tableName);

        return Function.Builder.create(this, lambdaName)
                .functionName(lambdaName)
                .memorySize(512)
                .timeout(Duration.seconds(3))
                .code(Code.fromAsset("lambdas/products/products-admin-lambda-1.0-SNAPSHOT.jar"))
                .runtime(Runtime.JAVA_11)
                .environment(envVariables)
                .build();
    }

    public Function getProductsFetchHandler() {
        return productsFetchHandler;
    }

    public Function getProductsAdminHandler() {
        return productsAdminHandler;
    }
}