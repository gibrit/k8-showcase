/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import java.util.Map.Entry;
import java.util.Set;
import org.saltuk.core.db.DbField;

/**
 *
 * @author saltuk
 */
public interface DBFieldFilter {

    static DBFieldFilter create(DbField field) {
        return new DBFieldFilterImpl(field);
    }

    Set<DBFilter> prepare(MultiMap map);
    
    Set<DBFilter> prepare(JsonObject data);

    DBFilter prepare(Entry<String, String> value);
    
    
}
