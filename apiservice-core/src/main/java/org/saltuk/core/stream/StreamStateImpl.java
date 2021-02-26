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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.saltuk.core.json.JsonDataBase;

/**
 *
 * @author saltuk
 */
class StreamStateImpl extends JsonDataBase implements StreamState {

    
    private long createTime;
    private long lastActivity;
    private final Set<String> tokens;

    public StreamStateImpl() {
        this.tokens = new HashSet<>();
    }

    
    @Override
    public JsonObject asJson() {
        return new JsonObject()
                .put("clients", new JsonArray(new ArrayList(this.tokens)))
                .put("create_time", this.createTime)
                .put("last_activity", this.lastActivity)
                
                ;
    }

    @Override
    public void fromJson(JsonObject value) {
        this.tokens.clear();                
        this.tokens.addAll(value.getJsonArray("clients", new JsonArray()).getList());
        this.createTime = value.getLong("create_time", System.currentTimeMillis());
        this.lastActivity = value.getLong("last_activity", System.currentTimeMillis());
    }

    @Override
    public int clients() {
        return this.tokens.size();
    }

    @Override
    public long createTime() {
        return this.createTime;
    }

    @Override
    public long lastActivity() {
        return this.lastActivity;
    }

    @Override
    public StreamState connect(String token) {
        this.tokens.add(token);
        this.updateActiveTime();
        return this;
    }

    @Override
    public StreamState disconnect(String token) {
        this.tokens.remove(token);
        this.updateActiveTime();
        return this;
    }

    @Override
    public StreamState updateActiveTime() {
        this.lastActivity = System.currentTimeMillis();
        return this;
    }

}
