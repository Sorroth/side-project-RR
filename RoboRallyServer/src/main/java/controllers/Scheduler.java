package controllers;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;
import rx.schedulers.Schedulers;

public class Scheduler extends BaseVerticle {

    @Override
    public void startVerticle(Future<Void> startFuture) {
        setupHandlers();
        startFuture.complete();
    }

    private void setupHandlers() {
        addHandler("get.scheduler-out", this::getSchedulerOut);
    }

    private void getSchedulerOut(Message<JsonObject> message) {
        JsonObject schedulerHistory = new JsonObject();
        Observable.just(schedulerHistory).map(out -> "example to be subscribed to")
                .subscribeOn(Schedulers.io()).map(out ->
                schedulerHistory.put("1", Thread.currentThread().getName()))
                .subscribeOn(Schedulers.computation()).map(out ->
                schedulerHistory.put("2", Thread.currentThread().getName()))
                .observeOn(Schedulers.computation()).map(out ->
                schedulerHistory.put("3", Thread.currentThread().getName()))
                .observeOn(Schedulers.io()).map(out ->
                schedulerHistory.put("4", Thread.currentThread().getName()))
                .subscribe(result -> message.reply(schedulerHistory));
    }
}

/*

1. observeOn works only downstream.
2. subscribeOn works downstream and upstream.
3. consecutive subscribeOns do not change the thread.
4. consequent observeOns do change the thread.
5. Thread change by an observeOn cannot be overridden by a subscribeOn.

Schedulers.io() 
Schedulers.computation() 
Schedulers.newThread() 
Schedulers.from(Executor executor) 
Main thread or AndroidSchedulers.mainThread() 
Schedulers.single() is new in RxJava 2. 
Schedulers.trampoline() 
 */