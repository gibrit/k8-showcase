package org.saltuk.core.json;

/*
 * Copyright 2016 saltuk.
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


import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public abstract class JsonDataBase implements JsonData {

    @Override
    public void writeToBuffer(Buffer buffer) {
        final JsonObject value = this.asJson();
        byte[] bytes = value.encode().getBytes(StandardCharsets.UTF_8);
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {

        final int len = buffer.getInt(pos);
        pos += 4;
        byte[] bytes = buffer.getBytes(pos, pos + len);
        pos += len;
        final JsonObject value = new JsonObject(new String(bytes, StandardCharsets.UTF_8));
        this.fromJson(value);

        return pos;
    }

}
