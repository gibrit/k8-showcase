/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.row;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saltuk
 */
public class DbRowSetImpl implements DbRowSet {
    
    private final RowSet<Row> rows;
    
    public DbRowSetImpl(RowSet<Row> rows) {
        this.rows = rows;
    }
    
    @Override
    public void prepareRow(Handler<AsyncResult<JsonArray>> handler) {
        List<String> names = this.rows.columnsNames();
        final JsonArray values = new JsonArray();
        RowIterator<Row> iterator = this.rows.iterator();
        List<Future> futures = new ArrayList();
        while (iterator.hasNext()) {
            
            Future<Void> futre = Future.future(h -> {
                Row next = iterator.next();
                DbRow.create(names, next).prepare(doPrepare -> {
                    if (doPrepare.succeeded()) {
                        values.add(doPrepare.result());
                        h.complete();
                    } else {
                        h.fail(doPrepare.cause());
                    }
                });
            });
            futures.add(futre);
        }
        CompositeFuture.all(futures).setHandler(doExecute -> {
            if (doExecute.succeeded()) {
                handler.handle(Future.succeededFuture(values));
            } else {
                handler.handle(Future.failedFuture(doExecute.cause()));
            }
        });
        
    }
    
}
