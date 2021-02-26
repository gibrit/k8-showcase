/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.saltuk.core.db.DbTable;

/**
 * Creates a CRUD Rest APi
 *
 * @author saltuk
 */
public interface ApiRouter {

    static ApiRouter create(Vertx vertx, String name, DbTable table) {
        return new ApiRouterImpl(vertx, name, table);

    }

    Router router();

}
