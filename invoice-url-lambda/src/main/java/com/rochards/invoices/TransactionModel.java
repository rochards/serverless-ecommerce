package com.rochards.invoices;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBTable(tableName = "InvoicesAndTransactionsTable")
public class TransactionModel {

    @DynamoDBHashKey(attributeName = "Pk")
    private String pk;
    @DynamoDBRangeKey(attributeName = "Sk")
    private String sk;
    @DynamoDBAttribute(attributeName = "Ttl")
    private Long ttl;
    @DynamoDBAttribute(attributeName = "ClientConnectionId")
    private String clientConnectionId;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "TransactionStatus")
    private Status status;

    public enum Status {
        URL_GENERATED, INVOICE_RECEIVED, INVOICE_PROCESSED
    }
}
