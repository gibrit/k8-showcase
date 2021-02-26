/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core;

import io.vertx.core.Vertx;

/**
 * Api Server
 *
 * @author saltuk
 */
public interface ApiServer {

    static ApiServer api(Vertx vertx, String serviceName, Class dataTable ) {
        return new ApiServerImpl(vertx, serviceName, dataTable );
    }

    ApiServer registerService(String name);

    void listen(int port);
    
    
}
