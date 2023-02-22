package com.rochards.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class OrderEventsLambda implements RequestHandler<SNSEvent, Void> {
    @Override
    public Void handleRequest(SNSEvent snsEvent, Context context) {
        return null;
    }
}
