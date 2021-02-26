/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db;

import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Tuple;
import java.util.List;

/**
 * Database Command Query
 *
 * @author saltuk
 */
public interface DbCommand {

    static DbCommand fail(JsonArray errors) {
        return new DbCommandImpl(errors);
    }

    static DbCommand sql(String sql, Tuple tuple) {
        return new DbCommandImpl(sql, tuple);
    }

    /**
     * Returns Errors
     *
     * @return
     */
    JsonArray errors();

    /**
     * Command is with Valid Parameters
     *
     * @return
     */
    boolean valid();

    /**
     * Returns Sql
     *
     * @return
     */
    String sql();

    /**
     * Returns Tuple
     *
     * @return
     */
    Tuple tuple();

}
