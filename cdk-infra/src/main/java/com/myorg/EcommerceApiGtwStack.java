package com.myorg;

import com.sun.tools.javac.util.List;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.HttpMethod;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

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

        RequestValidator requestValidator = RequestValidator.Builder.create(this, "ProductsValidator")
                .restApi(this.restApi)
                .requestValidatorName("ProductsValidator")
                .validateRequestBody(true)
                .build();

        /*
            endpoint /products
        */
        MethodOptions methodOptions = MethodOptions.builder()
                .requestValidator(requestValidator)
                .requestModels(defineProductMapModel())
                .build();
        Resource products = restApi.getRoot().addResource("products");
        products.addMethod(HttpMethod.POST.name(), productsAdminIntegration, methodOptions);

        /*
            endpoint /products/{id}
        */
        Resource productsId = products.addResource("{id}");
        productsId.addMethod(HttpMethod.GET.name(), productsFetchIntegration);
        productsId.addMethod(HttpMethod.PUT.name(), productsAdminIntegration, methodOptions);
        productsId.addMethod(HttpMethod.DELETE.name(), productsAdminIntegration);
    }

    private Map<String, IModel> defineProductMapModel() {
//        exemplo de como criar um mapeamento para a API e adicionar validacao
        Map<String, JsonSchema> productModelAttributes = new HashMap<>();
        productModelAttributes.put("name", JsonSchema.builder().type(JsonSchemaType.STRING).build());
        productModelAttributes.put("code", JsonSchema.builder().type(JsonSchemaType.STRING).build());
        productModelAttributes.put("model", JsonSchema.builder().type(JsonSchemaType.STRING).build());
        productModelAttributes.put("price", JsonSchema.builder().type(JsonSchemaType.NUMBER).build());

        Model productModel = Model.Builder.create(this, "ProductModel")
                .modelName("ProductModel")
                .restApi(this.restApi)
                .schema(JsonSchema.builder()
                        .schema(JsonSchemaVersion.DRAFT4)
                        .title("ProductModelRequest")
                        .type(JsonSchemaType.OBJECT)
                        .properties(productModelAttributes)
                        .required(List.of("name", "code", "model", "price"))
                        .build())
                .build();

        Map<String, IModel> mapProductModel = new HashMap<>();
        mapProductModel.put("application/json", productModel);
        return mapProductModel;
    }

    private void setUpOrdersIntegration(Function ordersHandler) {
        LambdaIntegration ordersIntegration = new LambdaIntegration(ordersHandler);

        RequestValidator requestValidator = RequestValidator.Builder.create(this, "OrdersValidator")
                        .restApi(this.restApi)
                        .requestValidatorName("OrdersValidator")
                        .validateRequestParameters(true)
                        .validateRequestBody(true)
                        .build();

        /* endpoint /orders */
        Map<String, IModel> mapOrderModel = defineOrderMapModel();
        Resource orders = restApi.getRoot().addResource("orders");
        orders.addMethod(HttpMethod.POST.name(), ordersIntegration,
                MethodOptions.builder()
                        .requestValidator(requestValidator)
                        .requestModels(mapOrderModel)
                        .build()
                );

        /*
         * para o DELETE e GET, vou indicar os parametros obrigatorios obrigatorios.
         * OBS.: o formato abaixo method.request.querystring.SEU_PARAMETRO é definido na doc do API gateway.
         */
        Map<String, Boolean> getParameters = new HashMap<>();
        getParameters.put("method.request.querystring.email", true);
        orders.addMethod(HttpMethod.GET.name(), ordersIntegration,
                MethodOptions.builder()
                        .requestParameters(getParameters)
                        .requestValidator(requestValidator)
                        .build()); // Ex.: /orders?email={email}

        Map<String, Boolean> deleteParameters = new HashMap<>();
        deleteParameters.put("method.request.querystring.email", true);
        deleteParameters.put("method.request.querystring.orderId", true);
        orders.addMethod(HttpMethod.DELETE.name(), ordersIntegration,
                MethodOptions.builder()
                        .requestParameters(deleteParameters)
                        .requestValidator(requestValidator)
                        .build());
    }

    private Map<String, IModel> defineOrderMapModel() {
//        exemplo de como criar um mapeamento para a API e adicionar validacao
        Map<String, JsonSchema> orderModelAttributes = new HashMap<>();
        orderModelAttributes.put("email", JsonSchema.builder().type(JsonSchemaType.STRING).build());
        orderModelAttributes.put("productsIds", JsonSchema.builder().type(JsonSchemaType.ARRAY)
                .minItems(1)
                .items(JsonSchema.builder().type(JsonSchemaType.STRING).build())
                .build());
        orderModelAttributes.put("paymentMethod", JsonSchema.builder().type(JsonSchemaType.STRING)
                .enumValue(List.of("CASH", "DEBIT_CARD", "CREDIT_CARD")).build());

        Model orderModel = Model.Builder.create(this, "OrderModel")
                .modelName("OrderModel")
                .restApi(this.restApi)
                .schema(JsonSchema.builder()
                        .schema(JsonSchemaVersion.DRAFT4)
                        .title("OrderModelRequest")
                        .type(JsonSchemaType.OBJECT)
                        .properties(orderModelAttributes)
                        .required(List.of("email", "productsIds", "paymentMethod"))
                        .build())
                .build();

        Map<String, IModel> mapOrderModel = new HashMap<>();
        mapOrderModel.put("application/json", orderModel);
        return mapOrderModel;
    }
}
