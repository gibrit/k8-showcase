/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.live;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.saltuk.core.StringUtils;
import org.saltuk.core.api.ApiResult;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.db.query.DbQuery;
import org.saltuk.core.db.query.filter.DBFilterGroup;
import org.saltuk.core.stream.StreamBase;
import org.saltuk.core.stream.StreamMessage;
import org.saltuk.core.stream.StreamState;
import org.saltuk.core.stream.StreamToken;
import org.saltuk.core.types.KubernetesDatabase;

/**
 *
 * @author saltuk
 */
public class DbLiveQueryImpl extends StreamBase<ApiResult> implements DbLiveQuery {
    
    private final Vertx vertx;
    
    private final DBFilterGroup filter;
    private final DbTable table;
    private final String queryId;
    
    public DbLiveQueryImpl(Vertx vertx, KubernetesDatabase db, DbTable table, DBFilterGroup filter) {
        super(vertx, StringUtils.append("live.query.", table.name(), ".", filter.token()), 300000L, (Handler<AsyncResult<ApiResult>> handler) -> {
            vertx.eventBus().consumer("db.live.query", view -> {
                String v = (String) view.body();
                if (table.name().equalsIgnoreCase(v)) {
                    DbQuery.create(vertx, table).select(db, filter, execute -> {
                        if (execute.succeeded()) {
                            handler.handle(Future.succeededFuture(execute.result()));
                        } else {
                            handler.handle(Future.failedFuture(execute.cause()));
                        }
                    });
                }
            });
        });
        this.vertx = vertx;
        this.table = table;
        this.filter = filter;
        this.queryId = filter.token();
        
    }
    
    @Override
    public String queryId() {
        return this.queryId;
    }
    
    @Override
    public DbLiveQuery connect(String userId, Handler<AsyncResult<StreamToken>> handler) {
        super.connect(userId, handler);
        return this;
    }
    
    @Override
    public DbLiveQuery onMessage(Handler<StreamMessage> handler) {
        super.onMessage(handler);
        return this;
    }
    
    @Override
    public DbLiveQuery onClose(Handler<Void> handler) {
        super.onClose(handler);
        return this;
    }
    
    @Override
    public DbLiveQuery onStatistics(Handler<StreamState> handler) {
        super.onStatistics(handler);
        return this;
    }
    
}
