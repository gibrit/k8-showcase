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
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import java.util.function.Consumer;
import org.saltuk.core.json.JsonData;

/**
 *
 * @author saltuk
 * @param <DATA>
 */
public interface Stream<DATA extends JsonData> extends JsonData {

    static <X extends JsonData> Stream<X> create(Vertx vertx, String address ,Consumer<Handler<AsyncResult<X>>> fnc) {
        return new StreamBase(vertx, address, -1, fnc);
    }

    static <X extends JsonData> Stream<X> create(Vertx vertx, String address, long time,Consumer<Handler<AsyncResult<X>>> fnc) {
        return new StreamBase(vertx, address, time, fnc);
    }

    /**
     * Stream Address
     *
     * @return
     */
    String address();

    /**
     * On Stream State Change
     *
     * @param handler
     * @return
     */
    Stream<DATA> onStatistics(Handler<StreamState> handler);

    /**
     * Start Stream
     *
     * @param handler
     */
    void start(Handler<AsyncResult<Void>> handler);

    /**
     * Closes Streams
     *
     */
    void close();

    Stream<DATA> onClose(Handler<Void> handler);

    Stream<DATA> onMessage(Handler<StreamMessage> handler);

    void publish(DATA data);

    <T> void send(DATA data, Handler<AsyncResult<Message<T>>> handler);

    Stream<DATA> connect(String userId, Handler<AsyncResult<StreamToken>> handler);

}
