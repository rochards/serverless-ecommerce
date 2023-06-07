package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketRouteOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.WebSocketStage;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.WebSocketLambdaIntegration;
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
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

import java.util.Collections;

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
        String stageName = "prod";
        WebSocketStage.Builder.create(this, "InvoiceWSApiStage")
                .webSocketApi(webSocketApi)
                .stageName(stageName)
                .autoDeploy(true)
                .build();

        Function invoiceURLHandler = configureInvoiceURLHandler(invoicesBucket, webSocketApi.getApiEndpoint(), stageName,
                invoicesAndTransactionsDdb.getTableArn());
        webSocketApi.grantManageConnections(invoiceURLHandler);
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

    private Function configureInvoiceURLHandler(Bucket invoicesBucket, String webSocketApiEndpoint, String stageName,
                                                String invoicesTableArn) {
        Function invoiceURLHandler = createLambda("InvoiceURLLambda",
                "com.rochards.invoices.InvoiceURLLambda",
                "lambdas/invoices/invoice-url-lambda-1.0-SNAPSHOT.jar"
        );
        invoiceURLHandler.addEnvironment("BUCKET_NAME", invoicesBucket.getBucketName());
        invoiceURLHandler.addEnvironment("INVOICE_WEBSOCKET_ENDPOINT", webSocketApiEndpoint + "/" + stageName);
        invoiceURLHandler.addToRolePolicy(
                /* No curso é colocada uma condicao mais granulada, porem fica trabalhoso colocar conditions em Java:
                 * {"ForAllValues:StringLike": {"dynamodb:LeadingKeys": ["#transaction"]}} -> indicando que essa lambda
                 * só vai escrever na tabela com a hash key começando com #transaction. */
                PolicyStatement.Builder.create()
                        .effect(Effect.ALLOW)
                        .actions(Collections.singletonList("dynamodb:PutItem"))
                        .resources(Collections.singletonList(invoicesTableArn))
                        .build()
        );
        invoiceURLHandler.addToRolePolicy(
                PolicyStatement.Builder.create()
                        .effect(Effect.ALLOW)
                        /* A lambda em si nao vai fazer put no bucket, mas apenas gerar a URL para quem a tiver fazer
                         * entao o Put. A gente precisa dar essa permissão para a Lambda para que o link tenha essa
                         * permissão. As permissões do link do bucket serão as mesmas de quem está o chamando*/
                        .actions(Collections.singletonList("s3:PutObject"))
                        .resources(Collections.singletonList(invoicesBucket.getBucketArn() + "/*"))
                        .build()
        );

        return invoiceURLHandler;
    }
}
