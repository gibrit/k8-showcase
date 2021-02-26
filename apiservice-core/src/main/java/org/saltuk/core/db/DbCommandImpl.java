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
 * DbCommand Implementation
 *
 * @author saltuk
 */
public class DbCommandImpl implements DbCommand {

    private final JsonArray errors;
    private final String sql;
    private Tuple tuple;

    public DbCommandImpl(JsonArray errors) {
        this.errors = errors;        
        this.sql=null;
    }

    public DbCommandImpl(String sql, Tuple tuple) {
        this.sql = sql;
        this.tuple = tuple;        
        this.errors = new JsonArray();
    }


    @Override
    public JsonArray errors() {
        return this.errors;
    }

    @Override
    public boolean valid() {
        return this.errors.size() == 0;
    }

    @Override
    public String sql() {
        return this.sql;
    }

    @Override
    public Tuple tuple() {
        return this.tuple;
    }

}
