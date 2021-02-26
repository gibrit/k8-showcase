/*
 * Copyright 2021 saltuk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.saltuk.core.stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import java.util.function.Consumer;
import org.saltuk.core.StringUtils;
import org.saltuk.core.api.ApiResult;
import org.saltuk.core.json.JsonData;
import org.saltuk.core.json.JsonDataBase;

/**
 * Stream Live Base
 *
 * @author saltuk
 * @param <DATA>
 *
 */
public class StreamBase<DATA extends JsonData> extends JsonDataBase implements Stream<DATA> {

    protected String name;
    private long timeout;
    private Handler<DATA> dataHandler;
    private Handler<StreamState> statisticHandler;

    protected StreamState statistics;

    private long timer;
    private Handler<Void> closeHandler;

    private Handler<StreamMessage> msgHandler;
    private final Vertx vertx;
    private final Consumer<Handler<AsyncResult<DATA>>> fnc;

    public StreamBase(Vertx vertx, String name, long timeout, Consumer<Handler<AsyncResult<DATA>>> handler) {
        this.name = name;
        this.timeout = timeout;
        this.statistics = StreamState.create();
        this.vertx = vertx;
        this.timer = -1;
        this.fnc = handler;
    }

    @Override
    public JsonObject asJson() {
        return new JsonObject().put("name", this.name).put("timeout", this.timeout).put("state", this.statistics.asJson());
    }

    @Override
    public void fromJson(JsonObject value) {
        this.timeout = value.getLong("timeout", 0L);
        this.name = value.getString("name", "failed");
        this.statistics = StreamState.create(value.getJsonObject("state", new JsonObject()));

    }

    @Override
    public Stream<DATA> onStatistics(Handler<StreamState> handler) {
        this.statisticHandler = handler;
        return this;
    }

    @Override
    public String address() {
        return this.name;
    }

    @Override
    public void start(Handler<AsyncResult<Void>> handler) {
        final EventBus eventbus = this.vertx.eventBus();
        eventbus.request(StringUtils.append(name, ".state"), new JsonObject(), new DeliveryOptions().setSendTimeout(12000), (AsyncResult<Message<Boolean>> e) -> {
            if (e.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                this.fnc.accept(v -> {
                    if (v.succeeded()) {
                        this.sendMessage(name, v.result().asJson());
                    } else {
                        this.sendMessage(name, ApiResult.fail(500, v.cause().getMessage()).asJson());
                    }
                });
                MessageConsumer<JsonObject> connectConsumer;
                connectConsumer = eventbus.consumer(StringUtils.append(name, ".connect"), doAction -> {
                    this.statistics.connect(doAction.headers().get("token"));
                    this.handleStatistic();
                });

                MessageConsumer<JsonObject> consumerDisconnect = eventbus.consumer(StringUtils.append(name, ".disconnect"), doAction -> {
                    this.statistics.disconnect(doAction.headers().get("token"));
                    this.handleStatistic();
                });
                MessageConsumer<JsonObject> consumerState = eventbus.consumer(StringUtils.append(name, ".state"), doAction -> {
                    doAction.reply(true);
                });

                MessageConsumer<JsonObject> consumerTokens = eventbus.consumer(StringUtils.append(name, ".tokens"), doAction -> {
                    StreamToken token = StreamToken.create(doAction.body());
                    MessageConsumer<JsonObject> consumer = eventbus.consumer(token.tokenAddress(name), checkToken -> {
                        checkToken.reply(token.asJson());
                    });
                    eventbus.consumer(StringUtils.append(name, ".leave"), close -> {
                        consumer.unregister();
                    });
                });

                MessageConsumer<JsonObject> consumerClose = eventbus.consumer(StringUtils.append(name, ".close"));
                consumerClose.handler(doAction -> {
                    consumerState.unregister();
                    connectConsumer.unregister();
                    consumerDisconnect.unregister();
                    consumerTokens.unregister();
                    consumerClose.unregister();
                    eventbus.publish(StringUtils.append(name, ".leave"), new JsonObject());
                });
                handler.handle(Future.succeededFuture());
            }
        });
    }

    @Override
    public void close() {
        this.sendMessage(StringUtils.append(name, ".close"), null);
        this.callCloseHandler();
    }

    protected void sendMessage(String address, JsonObject data) {
        this.vertx.eventBus().publish(address, data);
    }

    protected void handleStatistic() {
        if (this.statistics.clients() == 0) {
            if (this.timeout > 0) {
                this.timer = vertx.setTimer(timeout, doThickClose -> {
                    this.close();
                });
            } else {
                this.close();
            }
        } else {
            if (this.timer > 0) {
                vertx.cancelTimer(timer);
                this.timer = -1;
            }
        }
        if (this.statisticHandler != null) {
            this.statisticHandler.handle(statistics);
        }
    }

    @Override
    public Stream<DATA> onClose(Handler<Void> handler) {
        this.closeHandler = handler;
        return this;
    }

    @Override
    public Stream<DATA> onMessage(Handler<StreamMessage> handler) {
        this.msgHandler = handler;
        return this;
    }

    protected void callCloseHandler() {
        if (closeHandler != null) {
            this.closeHandler.handle(null);
        }
    }

    private void handleMessage(StreamMessage msg) {
        if (this.msgHandler != null) {
            this.msgHandler.handle(msg);
        } else {
            msg.reply(ApiResult.fail(403, "INVALID_REQUEST"));
        }
    }

    @Override
    public void publish(DATA data) {
        this.sendMessage(this.address(), data.asJson());
    }

    @Override
    public <T> void send(DATA data, Handler<AsyncResult<Message<T>>> handler) {
        this.vertx.eventBus().request(this.address(), data.asJson(), handler);
    }

    @Override
    public Stream<DATA> connect(String user, Handler<AsyncResult<StreamToken>> handler) {
        EventBus eventbus = this.vertx.eventBus();
        StreamToken token = StreamToken.create(user);
        eventbus.request(token.tokenAddress(name), new JsonObject(), new DeliveryOptions().setSendTimeout(12000), (AsyncResult<Message<JsonObject>> ex) -> {
            if (ex.succeeded()) {
                handler.handle(Future.succeededFuture(StreamToken.create(ex.result().body())));
            } else {

                eventbus.publish(StringUtils.append(name, ".tokens"), token.asJson());
                MessageConsumer<JsonObject> connectConsumer;
                connectConsumer = eventbus.consumer(StringUtils.append(token.address(), ".connect"), doAction -> {
                    String tok = doAction.headers().get("token");
                    if (token.token().equals(tok)) {
                        eventbus.publish(StringUtils.append(name, ".connect"), token.asJson(), new DeliveryOptions().addHeader("token", tok));
                    }
                });

                MessageConsumer<JsonObject> consumerDisconnect = eventbus.consumer(StringUtils.append(token.address(), ".disconnect"), doAction -> {
                    String tok = doAction.headers().get("token");
                    if (token.token().equals(tok)) {
                        eventbus.publish(StringUtils.append(name, ".disconnect"), doAction.body(), new DeliveryOptions().addHeader("token", tok));
                    }

                });

                MessageConsumer<JsonObject> consumerAction = eventbus.consumer(StringUtils.append(token.address(), ".action"), doAction -> {
                    String tok = doAction.headers().get("token");
                    if (token.token().equals(tok)) {
                        eventbus.publish(StringUtils.append(name, ".action"), doAction.body(), new DeliveryOptions().addHeader("token", tok));
                    }

                });
                MessageConsumer<JsonObject> consumerState = eventbus.consumer(StringUtils.append(token.address(), ".state"), doAction -> {
                    String tok = doAction.headers().get("token");
                    if (token.token().equals(tok)) {
                        eventbus.request(StringUtils.append(name, ".state"), doAction.body(), new DeliveryOptions().addHeader("token", tok), (AsyncResult<Message<Boolean>> e) -> {
                            if (e.succeeded()) {
                                doAction.reply(e.result().body());
                            } else {
                                doAction.reply(false);
                            }
                        });
                    } else {
                        doAction.reply(false);
                    }
                });
                MessageConsumer<JsonObject> consumerEvent = eventbus.consumer(name, doAction -> {
                    eventbus.publish(token.address(), doAction.body());
                });
                MessageConsumer<JsonObject> consumerClose = eventbus.consumer(StringUtils.append(token.address(), ".leave"));
                consumerClose.handler(doAction -> {
                    consumerState.unregister();
                    connectConsumer.unregister();
                    consumerDisconnect.unregister();
                    consumerEvent.unregister();
                    consumerClose.unregister();
                    consumerAction.unregister();
                });

                handler.handle(Future.succeededFuture(token));
            }
        });
        return this;
    }

    protected void readToken(EventBus eventbus, String user, Handler<AsyncResult<StreamToken>> handler) {
        final StreamToken token = StreamToken.create(user);
        eventbus.request(token.tokenAddress(name), new DeliveryOptions().setSendTimeout(120000), (AsyncResult<Message<JsonObject>> ex) -> {
            if (ex.succeeded()) {
                handler.handle(Future.succeededFuture(StreamToken.create(ex.result().body())));
            } else {
                handler.handle(Future.failedFuture(ex.cause()));
            }
        });
    }
}
