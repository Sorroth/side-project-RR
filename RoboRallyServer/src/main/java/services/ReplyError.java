package services;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

public class ReplyError {

    private final String HTTP_STATUS_CODE = "httpStatusCode";
    private final String FAIL = "fail";

    private Message<JsonObject> message;
    private DeliveryOptions deliveryOptions;

    public ReplyError(Message<JsonObject> message) {
        this.message = message;
        deliveryOptions = new DeliveryOptions();
    }

    public void replyError(int statusCode, String errorMessage) {
        message.fail(statusCode, errorMessage);
        deliveryOptions.addHeader(HTTP_STATUS_CODE, String.valueOf(statusCode));
        message.reply(FAIL, deliveryOptions);
    }
}

