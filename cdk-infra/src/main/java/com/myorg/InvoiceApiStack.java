package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public class InvoiceApiStack extends Stack {

    public InvoiceApiStack(Construct scope, String stackId) {
        super(scope, stackId, null);

        Table invoicesAndTransactionsDdb = createDynamoDBTable();
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
}
