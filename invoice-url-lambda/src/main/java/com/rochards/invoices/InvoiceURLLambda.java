package com.rochards.invoices;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class InvoiceURLLambda implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    private static final Logger LOGGER = LogManager.getLogger(InvoiceURLLambda.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String BUCKET_NAME;
    static {
        // variável passada no setup da função na classe InvoiceApiStack no projeto cdk-infra
        BUCKET_NAME = System.getenv("BUCKET_NAME");
    }

    private final AmazonS3 s3Client = AmazonS3Client.builder().build();
    private final TransactionRepository repository = new TransactionRepository();

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent input, Context context) {

        String clientConnectionId = input.getRequestContext().getConnectionId();
        LOGGER.info("ConnectionId: {} - Lambda RequestId: {}", clientConnectionId, context.getAwsRequestId());

        String fileKey = UUID.randomUUID().toString();
        int expirationBucketUrl = 300;
        var generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, fileKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(Date.from(Instant.now().plusSeconds(expirationBucketUrl)));

        URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        TransactionModel transaction = new TransactionModel();
        transaction.setPk("#transaction" + fileKey);
        transaction.setSk(fileKey);
        transaction.setTtl((long) expirationBucketUrl);
        transaction.setClientConnectionId(clientConnectionId);
        transaction.setStatus(TransactionModel.Status.URL_GENERATED);

        repository.save(transaction);

        var response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        response.setHeaders(Map.of("Content-Type", "application/json"));
        response.setBody(GSON.toJson(Map.of("message", "Ok")));

        return response;
    }
}