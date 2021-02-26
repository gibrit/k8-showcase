/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.json.JsonData;
import org.saltuk.core.stream.StreamToken;

/**
 * APi REQUEST Result
 *
 * @author saltuk
 */
public interface ApiResult extends JsonData {

    static ApiResult succcess(long total, JsonArray result, ApiPagination pagination) {
        return new ApiResultImpl(total, result, pagination);
    }

    static ApiResult created(JsonArray data) {
        return new ApiResultImpl(201, data, true);
    }

    static ApiResult update(JsonArray data) {
        return new ApiResultImpl(200, data, true);
    }

    static ApiResult deleted(JsonArray data) {
        return new ApiResultImpl(200, data, true);
    }

    static ApiResult fail(int code, String message) {
        return new ApiResultImpl(code, new JsonArray().add(message));
    }

    static ApiResult fail(int code, JsonArray message) {
        return new ApiResultImpl(code, message);
    }

    static ApiResult create(JsonObject data) {
        if (data != null && !data.isEmpty()) {
            ApiResultImpl result = new ApiResultImpl();
            result.fromJson(data);
            return result;
        }
        return null;
    }

    long total();

    JsonArray result();

    ApiPagination pagination();

    boolean success();

    int statusCode();

    JsonArray errorMessage();

    StreamToken liveToken();

    ApiResult liveToken(StreamToken token);
}
