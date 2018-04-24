package controllers;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;
import rx.functions.Action1;

public abstract class BaseVerticle extends AbstractVerticle {

    public abstract void startVerticle(Future<Void> startFuture);

    public void start(Future<Void> startFuture) {
        vertx.executeBlocking(future -> {
            try {
                startVerticle(startFuture);
                future.complete();
            } catch (Throwable t) {
                startFuture.fail(t);
                future.fail(t);
            }
        }, res -> {
            if (res.failed())
                failedVerticleStart(res.cause());
        });
    }

    protected void failedVerticleStart(Throwable throwable) {
        vertx.executeBlocking(event -> {
            vertx.close();
            System.exit(1);
        }, res -> { });
    }

    protected Observable<Message<JsonObject>> sendRequest(String eventBusAddress, JsonObject body) {
        return vertx.eventBus().<JsonObject>rxSend(eventBusAddress, body).toObservable();
    }

    protected void addHandler(String address, Action1<Message<JsonObject>> handler) {
        vertx.eventBus().<JsonObject>consumer(address).toObservable().subscribe(handler);
    }


//    public static JsonObject mapJ(Object... values) {
//        JsonObject json = new JsonObject();
//        if (values.length > 0 && values.length % 2 == 0) {
//            for (int idx = 0; idx < values.length; idx += 2) {
//                String name = (String) values[idx];
//                Object value = values[idx + 1];
//                json.put(name, value);
//            }
//        }
//        return json;
//    }
//
//    public static JsonArray listJ(Object... values) {
//        JsonArray list = new JsonArray();
//        if (values != null) {
//            for (Object object : values)
//                list.getList().add(object);
//        }
//        return list;
//    }

//    public Action1<Message<JsonObject>> routingContextWrapper(Action1<RoutingContext> handler) {
//        return message -> {
//            RoutingContext routingContext = RoutingContext.newInstance((io.vertx.ext.web.RoutingContext) message);
//            handler.call(routingContext);
//        };
//    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.vertx = new io.vertx.rxjava.core.Vertx(vertx);
    }
}
