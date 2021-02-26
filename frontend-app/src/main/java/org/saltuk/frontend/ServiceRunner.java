/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.frontend;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Showcase App
 *
 * @author saltuk
 */
public class ServiceRunner {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);
        router.route("/*").handler(StaticHandler.create().setIndexPage("/index.html"));
        vertx.createHttpServer().requestHandler(r -> {
            router.handle(r);
        }).listen(8080);
    }
}
