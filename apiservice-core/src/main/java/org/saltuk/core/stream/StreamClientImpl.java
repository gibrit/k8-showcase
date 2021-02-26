/*
 * Copyright 2021 Saltik Bugra Avci ben@saltuk.org
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
import org.saltuk.core.StringUtils;
import org.saltuk.core.api.ApiResult;

/**
 *
 * @author saltuk
 */
class StreamClientImpl implements StreamClient {

    private final String address;
    private final EventBus eventbus;
    

    public StreamClientImpl(Vertx vertx, String address) {
        this.address = address;
        this.eventbus = vertx.eventBus();
    }

    @Override
    public void connect(String token, Handler<Void> handler) {
        eventbus.publish(StringUtils.append(address, ".connect"), new JsonObject(), new DeliveryOptions().addHeader("token", token));
        handler.handle(null);
    }

    @Override
    public void disconnect(String token, Handler<Void> handler) {
        eventbus.publish(StringUtils.append(address, ".disconnect"), new JsonObject(), new DeliveryOptions().addHeader("token", token));
        handler.handle(null);
    }

    @Override
    public void checkState(String token, Handler<AsyncResult<Boolean>> handler) {
        eventbus.request(StringUtils.append(address, ".state"), new JsonObject(), new DeliveryOptions().addHeader("token", token), (AsyncResult<Message<Boolean>> e) -> {
            if (e.succeeded()) {
                handler.handle(Future.succeededFuture(e.result().body()));
            } else {
                handler.handle(Future.succeededFuture(false));
            }
        });
    }

    @Override
    public StreamClient onData(String token, Handler<ApiResult> handler) {
        MessageConsumer<JsonObject> consumer = eventbus.consumer(address, (Message<JsonObject> e) -> {
            handler.handle(ApiResult.create(e.body()));
        });
        return this;
    }

}
