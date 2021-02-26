/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.pubsub.PgSubscriber;

/**
 *
 * @author saltuk
 */
public interface KubernetesLiveDatabase {

    static KubernetesLiveDatabase create(Vertx vertx, PgSubscriber pg) {
        return new KubernetesLiveDatabaseImpl(vertx, pg);
    }

   
    void start(PgPool pool);
}
