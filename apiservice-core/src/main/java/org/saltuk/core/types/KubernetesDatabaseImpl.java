/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.pgclient.PgPool;
import io.vertx.redis.client.Redis;

/**
 *
 * @author saltuk
 */
public class KubernetesDatabaseImpl implements KubernetesDatabase {

    private PgPool pgPool;
    private final KubernetesDBType type;
    private Redis redis;
    private KubernetesLiveDatabase subscriber;
    private KubernetesLiveDatabase live;

    public KubernetesDatabaseImpl(PgPool pool, KubernetesLiveDatabase subscriber) {
        this.pgPool = pool;
        this.type = KubernetesDBType.POSTGRESQL;
        this.subscriber = subscriber;
        this.subscriber.start(pool);

    }

    KubernetesDatabaseImpl(Redis redis) {
        this.redis = redis;
        this.type = KubernetesDBType.REDIS;
    }

    @Override
    public KubernetesDBType type() {
        return this.type;
    }

    @Override
    public PgPool postgresql() {
        return this.pgPool;
    }

    @Override
    public KubernetesLiveDatabase live() {
        return this.live;
    }

    @Override
    public Redis redis() {
        return this.redis;
    }

}
