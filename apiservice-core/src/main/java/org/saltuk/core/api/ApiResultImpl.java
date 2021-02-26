/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.json.JsonDataBase;
import org.saltuk.core.stream.StreamToken;

/**
 *
 * @author saltuk
 */
class ApiResultImpl extends JsonDataBase implements ApiResult {

    private ApiPagination pagination;
    private boolean success;
    private int statusCode;
    private JsonArray errorMessages;
    private JsonArray result;
    private long total;
    private StreamToken liveToken;

    public ApiResultImpl() {
    }

    ApiResultImpl(long total, JsonArray result, ApiPagination pagination) {
        this.success = true;
        this.total = total;
        this.result = result;
        this.pagination = pagination;
        this.statusCode = 200;
        this.errorMessages = new JsonArray();
    }

    ApiResultImpl(int code, JsonArray message) {
        this.statusCode = code;
        this.errorMessages = message;
        this.success = false;
    }

    ApiResultImpl(int i, JsonArray data, boolean success) {
        this.success = success;
        this.statusCode = i;
        this.result = data;
    }

    @Override
    public JsonObject asJson() {
        final JsonObject res = new JsonObject();
        res.put("success", this.success);
        res.put("status", this.statusCode);
        if (this.success) {
            JsonObject meta = new JsonObject();
            if (this.liveToken != null) {
                meta.put("token_live", this.liveToken.asJson());
            }
            if (this.pagination != null) {

                meta.put("pagianation", this.pagination.asJson())
                        .put("total", this.total);

            }
            if (!meta.isEmpty()) {
                res.put("meta", meta);
            }
            res.put("data", this.result);
        } else {
            res.put("errors", this.errorMessages);
        }
        return res;
    }

    @Override
    public void fromJson(JsonObject value) {
        this.success = value.getBoolean("success", false);
        this.statusCode = value.getInteger("status", 500);
        if (success) {
            JsonObject meta = value.getJsonObject("meta", new JsonObject());
            this.liveToken = StreamToken.create(meta.getJsonObject("live_token", new JsonObject()));
            this.total = meta.getLong("total", 0L);
            this.pagination = ApiPagination.create(meta.getJsonObject("pagination", new JsonObject()));
        } else {
            this.errorMessages = value.getJsonArray("errors", new JsonArray());
        }
    }

    @Override
    public long total() {
        return this.total;
    }

    @Override
    public JsonArray result() {
        return this.result;
    }

    @Override
    public ApiPagination pagination() {
        return this.pagination;
    }

    @Override
    public boolean success() {
        return this.success;
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public JsonArray errorMessage() {
        return this.errorMessages;
    }

    @Override
    public StreamToken liveToken() {
        return this.liveToken;
    }

    @Override
    public ApiResult liveToken(StreamToken liveToken) {
        this.liveToken = liveToken;
        return this;
    }

}
