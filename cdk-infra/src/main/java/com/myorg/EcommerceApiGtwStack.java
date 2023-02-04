package com.myorg;

import java.util.Map;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.AccessLogFormat;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.LogGroupLogDestination;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.RequestValidatorOptions;
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
                                Function productsAdminHandler, Function ordersHandler) {
        super(scope, stackId, null);

        restApi = setUpRestApi();
        setUpProductsIntegrations(productsFetchHandler, productsAdminHandler);
        setUpOrdersIntegration(ordersHandler);
    }

    private RestApi setUpRestApi() {
        return RestApi.Builder.create(this, ECOMMERCE_API + "RestApiId")
                .restApiName(ECOMMERCE_API + "RestAPiName")
                .deployOptions(configStageOptions())
                .build();
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

    private void setUpProductsIntegrations(Function productsFetchHandler, Function productsAdminHandler) {
        LambdaIntegration productsFetchIntegration = new LambdaIntegration(productsFetchHandler);
        LambdaIntegration productsAdminIntegration = new LambdaIntegration(productsAdminHandler);

        /*
            endpoint /products
        */
        Resource products = restApi.getRoot().addResource("products");
        products.addMethod(HttpMethod.POST.name(), productsAdminIntegration);

        /*
            endpoint /products/{id}
        */
        Resource productsId = products.addResource("{id}");
        productsId.addMethod(HttpMethod.GET.name(), productsFetchIntegration);
        productsId.addMethod(HttpMethod.PUT.name(), productsAdminIntegration);
        productsId.addMethod(HttpMethod.DELETE.name(), productsAdminIntegration);
    }

    private void setUpOrdersIntegration(Function ordersHandler) {
        LambdaIntegration ordersIntegration = new LambdaIntegration(ordersHandler);

        /* endpoint /orders */
        Resource orders = restApi.getRoot().addResource("orders");
        orders.addMethod(HttpMethod.POST.name(), ordersIntegration);
        /*
         * para o DELETE e GET, vou indicar os parametros obrigatorios obrigatorios.
         * OBS.: o formato abaixo method.request.querystring.SEU_PARAMETRO é definido na doc do API gateway.
         */
        orders.addMethod(HttpMethod.GET.name(), ordersIntegration,
                MethodOptions.builder()
                        .requestParameters(Map.of(
                                "method.request.querystring.email", true))
                        .requestValidatorOptions(
                                RequestValidatorOptions.builder()
                                        .requestValidatorName("OrdersGetValidator")
                                        .validateRequestParameters(true)
                                        .build())
                        .build()); // Ex.: /orders?email={email}
        orders.addMethod(HttpMethod.DELETE.name(), ordersIntegration,
                MethodOptions.builder()
                        .requestParameters(Map.of(
                                "method.request.querystring.email", true,
                                "method.request.querystring.orderId", true))
                        .requestValidatorOptions(
                                RequestValidatorOptions.builder()
                                        .requestValidatorName("OrdersDeletionValidator")
                                        .validateRequestParameters(true)
                                        .build())
                        .build());
    }
}
