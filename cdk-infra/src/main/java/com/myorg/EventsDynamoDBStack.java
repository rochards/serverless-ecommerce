package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.*;
import software.constructs.Construct;

public class EventsDynamoDBStack extends Stack {

    private final Table eventsTable;

    public EventsDynamoDBStack(Construct scope, String stackId) {
        super(scope, stackId, null);
        this.eventsTable = createTable();
    }

    private Table createTable() {
        String tableName = "EventsTable";
        Table table = Table.Builder.create(this, tableName)
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

        table.autoScaleReadCapacity(
                EnableScalingProps.builder()
                        .minCapacity(3)
                        .maxCapacity(5)
                        .build()
        ).scaleOnUtilization(
                UtilizationScalingProps.builder()
                        .targetUtilizationPercent(75)
                        .scaleInCooldown(Duration.minutes(5))
                        .scaleOutCooldown(Duration.minutes(1))
                        .build()
        );

        table.addGlobalSecondaryIndex(
                GlobalSecondaryIndexProps.builder()
                        .indexName("EmailAndEventTypeIndex")
                        .partitionKey(
                                Attribute.builder()
                                        .name("Email")
                                        .type(AttributeType.STRING)
                                        .build()
                        )
                        .sortKey(
                                Attribute.builder()
                                        .name("EventType")
                                        .type(AttributeType.STRING)
                                        .build()
                        )
                        .projectionType(ProjectionType.ALL)
                        .build());
        return table;
    }

    public Table getEventsTable() {
        return eventsTable;
    }
}
