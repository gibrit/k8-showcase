/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.saltuk.core.StringUtils;
import org.saltuk.core.json.JsonDataBase;

/**
 *
 * @author saltuk
 */
class ApiPaginationImpl extends JsonDataBase implements ApiPagination {

    private int offset;
    private int limit;
    private String token;

    public ApiPaginationImpl() {
    }

    ApiPaginationImpl(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public JsonObject asJson() {
        return new JsonObject()
                .put("pg_offset", this.offset)
                .put("pg_limit", this.limit);
    }

    @Override
    public void fromJson(JsonObject value) {
        this.limit = value.getInteger("pg_limit", 20);
        this.offset = value.getInteger("pg_offset", 0);
    }

    @Override
    public int offset() {
        return this.offset;

    }

    @Override
    public int limit() {
        return this.limit;
    }

    @Override
    public String token() {
        if (StringUtils.isEmpty(token)) {
            StringBuilder output = new StringBuilder();
            output.append("pg~").append(this.limit).append("~").append(this.offset);
            this.token = DigestUtils.md5Hex(output.toString());
        }
        return this.token;
    }

}
