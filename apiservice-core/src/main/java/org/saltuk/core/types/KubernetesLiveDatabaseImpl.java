/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.pubsub.PgSubscriber;

/**
 *
 * @author saltuk
 */
public class KubernetesLiveDatabaseImpl implements KubernetesLiveDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesLiveDatabase.class.getName());
    private final PgSubscriber pg;
    private final Vertx vertx;
    private final String CHANNEL = "pg_db_live_db";

    public KubernetesLiveDatabaseImpl(Vertx vertx, PgSubscriber pg) {
        this.pg = pg;
        this.vertx = vertx;
    }

    @Override
    public void start(PgPool pool) {
        pool
                .query("LISTEN " + CHANNEL)
                .execute(ar -> {
                    if (ar.succeeded()) {
                        this.pg.connect(start -> {
                            if (start.succeeded()) {

                                this.pg.channel(CHANNEL)
                                        .exceptionHandler(e -> {
                                            LOGGER.error("Kubernetes Live Database Error:", e);
                                        }).endHandler(v -> {

                                    LOGGER.info("Kubernetes Live Database is end");

                                }).handler(e -> {
                                    LOGGER.info("Kubernetes Live Database Trigged for " + e);
                                    this.vertx.eventBus().publish("db.live.query", e);
                                });
                            } else {
                                LOGGER.error("Kubernetes Live Database not started:", start.cause());
                            }
                        });
                    } else {
                        LOGGER.error("Kubernetes Live Database  Listen not started", ar.cause());
                    }
                });
    }

}
