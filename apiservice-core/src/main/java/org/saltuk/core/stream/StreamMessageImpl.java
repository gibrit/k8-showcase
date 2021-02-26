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

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.api.ApiResult;


/**
 *
 * @author saltuk
 */
public class StreamMessageImpl implements StreamMessage {

    private final String token;
    private final JsonObject body;
    private final Message<JsonObject> base;

    public StreamMessageImpl(Message<JsonObject> message) {
        this.token = message.headers().get("token");
        this.body = message.body();
        this.base = message;
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public JsonObject message() {
        return this.body;
    }

    @Override
    public void reply(ApiResult result) {
        this.base.reply(result.asJson());
    }

}
