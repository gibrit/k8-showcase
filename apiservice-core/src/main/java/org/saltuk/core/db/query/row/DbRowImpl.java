/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.row;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import java.util.List;

/**
 *
 * @author saltuk
 */
public class DbRowImpl implements DbRow {
    
    private final List<String> names;
    private final Row row;
    
    public DbRowImpl(List<String> names, Row row) {
        this.names = names;        
        this.row = row;
    }
    
    @Override
    public void prepare(Handler<AsyncResult<JsonObject>> handler) {
        final JsonObject res = new JsonObject();
        this.names.forEach(v -> {
            res.put(v, row.getValue(v));
        });
        handler.handle(Future.succeededFuture(res));
    }
    
}
