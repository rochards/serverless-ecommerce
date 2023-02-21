package com.rochards.orders;

public class OrderTopic {
    private Type type;
    private String data;

    private enum Type {
        ORDER_CREATED, ORDER_DELETED
    }
}
