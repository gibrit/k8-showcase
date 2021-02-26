/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.StringUtils;
import org.saltuk.core.json.JsonData;

/**
 * Api Pagination
 *
 * @author saltuk
 */
public interface ApiPagination extends JsonData {

    /**
     * Create from Request
     *
     * @param map
     * @return
     */
    static ApiPagination create(MultiMap map) {

        String offset = map.get("pg_offset");
        String count = map.get("pg_limit");
        int o = StringUtils.isEmpty(offset) ? 0 : Integer.valueOf(offset.trim());
        int c = StringUtils.isEmpty(count) ? 20 : Integer.valueOf(count.trim());
        return new ApiPaginationImpl(o, c);
    }

    /**
     * Create From JSonOBject 
     * @param data
     * @return 
     */
    static ApiPagination create(JsonObject data) {
        if (data != null && !data.isEmpty()) {
            ApiPaginationImpl result = new ApiPaginationImpl();
            result.fromJson(data);
            return result;
        }
        return null;
    }

    /**
     * Returns Offset
     *
     * @return
     */
    int offset();

    /**
     * return Count
     *
     * @return
     */
    int limit();
    
    String token(); 
}
