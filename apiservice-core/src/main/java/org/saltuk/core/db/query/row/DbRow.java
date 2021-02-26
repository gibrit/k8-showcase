/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.row;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import java.util.List;

/**
 * Database Row Data
 *
 * @author saltuk
 */
public interface DbRow {

    static DbRow create(List<String> names, Row row) {
        return new DbRowImpl(names, row);
    }

    void prepare(Handler<AsyncResult<JsonObject>> handler);
}
