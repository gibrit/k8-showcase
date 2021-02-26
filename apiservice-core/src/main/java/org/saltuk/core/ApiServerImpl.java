/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core;

import io.vertx.core.Vertx;
import java.util.HashSet;
import java.util.Set;
import org.saltuk.core.api.ApiRouter;
import org.saltuk.core.db.DbManager;
import org.saltuk.core.db.DbTable;

/**
 *
 * @author saltuk
 */
public class ApiServerImpl implements ApiServer {

    private final DbTable dataTable;
    private final Vertx vertx;
    private final String name;
    private final boolean isApi;
    private final Set<String> services;

    public ApiServerImpl(Vertx vertx, String name, Class dataTable, boolean isApi) {
        this.vertx = vertx;
        this.name = name;
        this.dataTable = DbManager.newIstance().build(dataTable);
        this.isApi = isApi;
        this.services = new HashSet<>();
    }

    public ApiServerImpl(Vertx vertx, String name, Class dataTable) {
        this(vertx, name, dataTable, true);
    }

    @Override
    public void listen(int port) {
        if (this.isApi) {
            ApiRouter router = ApiRouter.create(vertx, name, dataTable);

            this.vertx.createHttpServer().requestHandler(readReq -> {
                router.router().handle(readReq);
            }).listen(port);
        } else {

        }
    }

    @Override
    public ApiServer registerService(String name) {
        this.services.add(name);
        return this;
    }

}
