/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.live;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

/**
 *
 * @author saltuk
 */
public interface DbLiveQueryHandler {

    static DbLiveQueryHandler create(Vertx vertx) {
        return new DbLiveQueryHandlerImpl(vertx);
    }

    Router handler(SockJSHandlerOptions options);

}
