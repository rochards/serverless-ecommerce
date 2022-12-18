package com.myorg;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;

public class EventsDynamoDBStack extends Stack {

    private final Table eventsTable;

    public EventsDynamoDBStack(Construct scope, String id) {
        super(scope, id, null);
        this.eventsTable = createTable();
    }

    private Table createTable() {
        String tableName = "EventsTable";
        return Table.Builder.create(this, tableName)
                .tableName(tableName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .billingMode(BillingMode.PROVISIONED)
                .writeCapacity(3)
                .readCapacity(3)
                .partitionKey(
                        Attribute.builder()
                                .name("Code")
                                .type(AttributeType.STRING)
                                .build()
                )
                .sortKey(
                        Attribute.builder()
                                .name("EventTypeAndTimestamp")
                                .type(AttributeType.STRING)
                                .build()
                )
                .timeToLiveAttribute("Ttl")
                .build();
    }
}
