package com.myorg;

import software.amazon.awscdk.App;

public class EcommerceApplicationInfra {
    public static void main(final String[] args) {
        App app = new App();

        EventsDynamoDBStack eventsDynamoDBStack = new EventsDynamoDBStack(app, "EventsDynamoDBStack");
        ProductsAppStack productsAppStack = new ProductsAppStack(app, "ProductsAppStack", eventsDynamoDBStack.getEventsTable());

        EcommerceApiGtwStack ecommerceApiGtwStack =
                new EcommerceApiGtwStack(app, "EcommerceApiGtwStack", productsAppStack.getProductsFetchHandler(),
                        productsAppStack.getProductsAdminHandler());
        ecommerceApiGtwStack.addDependency(productsAppStack);

        app.synth();
    }
}

