/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.json.JsonData;

/**
 * Api Body Request
 *
 * @author saltuk
 */
public interface ApiBodyRequest extends JsonData {

    static ApiBodyRequest createJsonData(JsonObject value) {
        return new ApiBodyRequestImpl(false, value.toBuffer());
    }

    static ApiBodyRequest createJsonData(JsonArray value) {
        return new ApiBodyRequestImpl(true, value.toBuffer());
    }

    static ApiBodyRequest create(JsonObject value) {
        if (value != null && !value.isEmpty()) {
            final ApiBodyRequestImpl result = new ApiBodyRequestImpl();
            result.fromJson(value);
            return result;

        }
        return null;
    }

    boolean multiple();

    Buffer payload();
}
