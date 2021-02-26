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

import io.vertx.core.json.JsonObject;
import org.saltuk.core.json.JsonData;

/**
 *
 * @author saltuk
 */
public interface StreamState extends JsonData {

    static StreamState create() {
        return new StreamStateImpl();
    }

    static StreamState create(JsonObject data) {
        if (data != null && !data.isEmpty()) {
            StreamStateImpl result = new StreamStateImpl();
            result.fromJson(data);
            return result;
        }
        return null;
    }

    int clients();

    long createTime();

    long lastActivity();

    StreamState connect(String token);

    StreamState disconnect(String token);

    StreamState updateActiveTime();
}
