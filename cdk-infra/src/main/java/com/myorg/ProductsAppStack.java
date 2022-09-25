package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class ProductsAppStack extends Stack {

    private final Function productsFetchHandler;

    public ProductsAppStack(Construct scope, String stackId) {
        super(scope, stackId, null);

        this.productsFetchHandler = createProductsFetchLambda();
    }

    private Function createProductsFetchLambda() {
        return Function.Builder.create(this, "ProductsFetchLambda")
                .functionName("ProductsFetchLambda")
                .handler("products.ProductsFetchLambda") /* igual ao do projeto products-lambda. É permitido referenciar
                                                            o pacote.nome_da_classe pq esta implementa a interface RequestHandler */
                .memorySize(512)
                .timeout(Duration.seconds(3))
                .code(Code.fromAsset("lambdas/products/products-lambda-1.0-SNAPSHOT.jar"))
                .runtime(Runtime.JAVA_11)
                .build();
    }

    public Function getProductsFetchHandler() {
        return productsFetchHandler;
    }
}
