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
import software.amazon.awscdk.services.lambda.HttpMethod;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

public class EcommerceApiGtwStack extends Stack {

    private static final String ECOMMERCE_API = "ECommerceAPI";
    private final RestApi restApi;


    public EcommerceApiGtwStack(Construct scope, String stackId, Function productsFetchHandler,
                                Function productsAdminHandler) {
        super(scope, stackId, null);

        restApi = setUpRestApi();
        setUpProductsIntegrations(productsFetchHandler, productsAdminHandler);
    }

    private RestApi setUpRestApi() {
        return RestApi.Builder.create(this, ECOMMERCE_API + "RestApiId")
                .restApiName(ECOMMERCE_API + "RestAPiName")
                .deployOptions(configStageOptions())
                .build();
    }

    private void setUpProductsIntegrations(Function productsFetchHandler, Function productsAdminHandler) {
        LambdaIntegration productsFetchIntegration = new LambdaIntegration(productsFetchHandler);
        LambdaIntegration productsAdminIntegration = new LambdaIntegration(productsAdminHandler);

        /*
            endpoint /products
        */
        Resource products = restApi.getRoot().addResource("products");
        products.addMethod(HttpMethod.GET.name(), productsFetchIntegration);
        products.addMethod(HttpMethod.POST.name(), productsAdminIntegration);

        /*
            endpoint /products/{id}
        */
        Resource productsId = products.addResource("{id}");
        productsId.addMethod(HttpMethod.GET.name(), productsFetchIntegration);
        productsId.addMethod(HttpMethod.PUT.name(), productsAdminIntegration);
        productsId.addMethod(HttpMethod.DELETE.name(), productsAdminIntegration);
    }

    private StageOptions configStageOptions() {
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
