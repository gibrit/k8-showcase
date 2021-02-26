/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.row;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 *DataBase Row 
 * @author saltuk
 */
public interface DbRowSet {
    
    static   DbRowSet  create(RowSet<Row> rows){
        return  new DbRowSetImpl(rows);
    }
    
    void  prepareRow(Handler<AsyncResult<JsonArray>> handler); 
}
