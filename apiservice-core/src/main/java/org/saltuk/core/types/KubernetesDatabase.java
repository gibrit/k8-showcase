/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.pgclient.PgPool;
import io.vertx.redis.client.Redis;

/**
 * K8 Database Container
 *
 * @author saltuk
 */
public interface KubernetesDatabase {

    static KubernetesDatabase postgreSql(PgPool pool, KubernetesLiveDatabase subsciber) {
        return new KubernetesDatabaseImpl(pool, subsciber);
    }

    static KubernetesDatabase redis(Redis redis) {
        return new KubernetesDatabaseImpl(redis);
    }

    /**
     * Database Type
     *
     * @return
     */
    KubernetesDBType type();

    /**
     * PostgreSql Pool
     *
     * @return
     */
    PgPool postgresql();

    KubernetesLiveDatabase live();

    /**
     * Redis
     *
     * @return
     */
    Redis redis();

}
