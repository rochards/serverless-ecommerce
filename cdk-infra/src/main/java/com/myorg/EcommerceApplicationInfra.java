package com.myorg;

import software.amazon.awscdk.App;

public class EcommerceApplicationInfra {
    public static void main(final String[] args) {
        App app = new App();

        EventsDynamoDBStack eventsDynamoDBStack = new EventsDynamoDBStack(app, "EventsDynamoDBStack");
        
        ProductsAppStack productsAppStack = new ProductsAppStack(app, "ProductsAppStack", eventsDynamoDBStack.getEventsTable());
        productsAppStack.addDependency(eventsDynamoDBStack);

        OrdersAppStack ordersAppStack = new OrdersAppStack(app, "OrdersAppStack", productsAppStack.getProductsDdbTable()
                , eventsDynamoDBStack.getEventsTable());
        ordersAppStack.addDependency(productsAppStack);
        ordersAppStack.addDependency(eventsDynamoDBStack);

        EcommerceApiGtwStack ecommerceApiGtwStack =
                new EcommerceApiGtwStack(app, "EcommerceApiGtwStack", productsAppStack.getProductsFetchHandler(),
                        productsAppStack.getProductsAdminHandler(), ordersAppStack.getOrdersHandler(),
                        ordersAppStack.getOrdersEventsFetchHandler());
        ecommerceApiGtwStack.addDependency(productsAppStack);
        ecommerceApiGtwStack.addDependency(ordersAppStack);

        app.synth();
    }
}

