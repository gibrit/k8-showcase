/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.json.JsonDataBase;

/**
 *
 * @author saltuk
 */
public class ApiBodyRequestImpl extends JsonDataBase implements ApiBodyRequest {

    private boolean multiple;
    private Buffer payload;

    public ApiBodyRequestImpl() {
    }

    
    public ApiBodyRequestImpl(boolean multiple, Buffer payload) {
        this.multiple = multiple;
        this.payload = payload;
    }

    @Override
    public boolean multiple() {
        return this.multiple;
    }

    @Override
    public Buffer payload() {
        return this.payload;
    }

    @Override
    public JsonObject asJson() {
        return new JsonObject()
                .put("multiple", multiple)
                .put("payload", payload);
    }

    @Override
    public void fromJson(JsonObject value) {
        this.multiple = value.getBoolean("multiple", false);
        if (this.multiple) {
            this.payload = value.getJsonArray("payload", new JsonArray()).toBuffer();
        } else {
            this.payload = value.getJsonObject("payload", new JsonObject()).toBuffer();
        }

    }

}
