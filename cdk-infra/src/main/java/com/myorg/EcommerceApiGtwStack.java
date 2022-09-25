package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.AccessLogFormat;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.LogGroupLogDestination;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.StageOptions;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

public class EcommerceApiGtwStack extends Stack {

    private static final String ECOMMERCE_API = "ECommerceAPI";
    private final RestApi restApi;


    public EcommerceApiGtwStack(Construct scope, String stackId, Function productsFetchHandler) {
        super(scope, stackId, null);

        restApi = setUpRestApi();
        setUpProductsFetchIntegration(productsFetchHandler);
    }

    private RestApi setUpRestApi() {
        return RestApi.Builder.create(this, ECOMMERCE_API + "RestApiId")
                .restApiName(ECOMMERCE_API + "RestAPiName")
                .deployOptions(configLogs())
                .build();
    }

    private void setUpProductsFetchIntegration(Function productsFetchHandler) {
        LambdaIntegration fetchProductsIntegration = new LambdaIntegration(productsFetchHandler);
        /* endpoint /products */
        Resource products = restApi.getRoot().addResource("products");
        products.addMethod("GET", fetchProductsIntegration);
    }

    private StageOptions configLogs() {
        LogGroup logGroup = LogGroup.Builder.create(this, ECOMMERCE_API + "LogGroupId")
                .logGroupName(ECOMMERCE_API + "LogGroupName")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        return StageOptions.builder()
                .accessLogDestination(new LogGroupLogDestination(logGroup))
                .accessLogFormat(AccessLogFormat.jsonWithStandardFields())
                .build();
    }
}
