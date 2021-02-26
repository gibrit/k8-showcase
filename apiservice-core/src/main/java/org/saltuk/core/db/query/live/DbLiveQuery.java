/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.live;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.saltuk.core.api.ApiResult;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.db.query.filter.DBFilterGroup;
import org.saltuk.core.stream.Stream;
import org.saltuk.core.stream.StreamMessage;
import org.saltuk.core.stream.StreamState;
import org.saltuk.core.stream.StreamToken;
import org.saltuk.core.types.KubernetesDatabase;

/**
 *
 * @author saltuk
 */
public interface DbLiveQuery extends Stream<ApiResult> {

    static DbLiveQuery create(Vertx vertx, KubernetesDatabase db, DbTable table, DBFilterGroup filter) {
        return new DbLiveQueryImpl(vertx, db, table, filter);
    }

    String queryId();

    @Override
    DbLiveQuery connect(String userId, Handler<AsyncResult<StreamToken>> handler);

    @Override
    DbLiveQuery onMessage(Handler<StreamMessage> handler);

    @Override
    DbLiveQuery onClose(Handler<Void> handler);

    @Override
    DbLiveQuery onStatistics(Handler<StreamState> handler);

}
