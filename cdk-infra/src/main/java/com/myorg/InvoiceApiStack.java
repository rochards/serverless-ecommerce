package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketRouteOptions;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.WebSocketLambdaIntegration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

public class InvoiceApiStack extends Stack {

    public InvoiceApiStack(Construct scope, String stackId) {
        super(scope, stackId, null);

        Table invoicesAndTransactionsDdb = createDynamoDBTable();
        Bucket invoicesBucket = createBucket();

        // para o WebSocket
        Function connectionHandler = createLambda("WebSocketConnectionLambda",
                "com.rochards.invoices.WebSocketConnectionLambda",
                "lambdas/invoices/websocket-connection-lambda-1.0-SNAPSHOT.jar"
        );
        Function disconnectionHandler = createLambda("WebSocketDisconnectionLambda",
                "com.rochards.invoices.WebSocketDisconnectionLambda",
                "lambdas/invoices/websocket-disconnection-lambda-1.0-SNAPSHOT.jar"
        );

        WebSocketApi webSocketApi = createWebSocket(connectionHandler, disconnectionHandler);

    }

    private Table createDynamoDBTable() {
        return Table.Builder.create(this, "InvoicesAndTransactionsTable")
                .tableName("InvoicesAndTransactionsTable")
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(3)
                .writeCapacity(3)
                .removalPolicy(RemovalPolicy.DESTROY)
                .partitionKey(
                        Attribute.builder()
                                .name("Pk")
                                .type(AttributeType.STRING)
                                .build()
                )
                .sortKey(
                        Attribute.builder()
                                .name("Sk")
                                .type(AttributeType.STRING)
                                .build()
                )
                .timeToLiveAttribute("Ttl")
                .build();
    }

    private Bucket createBucket() {
        return Bucket.Builder.create(this, "InvoicesBucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(Boolean.TRUE)
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
                .build();
    }

    private WebSocketApi createWebSocket(Function connectionHandler, Function disconnectionHandler) {
        return WebSocketApi.Builder.create(this, "InvoiceWSApi")
                .apiName("InvoiceWebSocketApi")
                .connectRouteOptions(
                        WebSocketRouteOptions.builder()
                                .integration(new WebSocketLambdaIntegration("WebSocketConnectionHandler", connectionHandler))
                                .build()
                )
                .disconnectRouteOptions(
                        WebSocketRouteOptions.builder()
                                .integration(new WebSocketLambdaIntegration("WebSocketDisconnectionHandler", disconnectionHandler))
                                .build()
                )
                .build();
    }
}
