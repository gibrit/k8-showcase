/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.api.ApiBodyRequest;
import org.saltuk.core.api.ApiResult;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.db.query.filter.DBFilterGroup;
import org.saltuk.core.types.KubernetesDatabase;

/**
 * DbQuery For Database
 *
 *
 *
 * @author Saltik Bugra Avci ben@saltuk.org
 */
public interface DbQuery {

    static DbQuery create(Vertx vertx, DbTable table) {
        return new DbQueryImpl(vertx, table);
    }

    DbTable table();

    void select(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler);

    void select(KubernetesDatabase db, DBFilterGroup filter, Handler<AsyncResult<ApiResult>> handler);

    void selectLive(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler);

    void select(KubernetesDatabase db, JsonObject map, Handler<AsyncResult<ApiResult>> handler);

    void select(KubernetesDatabase db, long id, MultiMap map, Handler<AsyncResult<ApiResult>> handler);

    void select(KubernetesDatabase db, long id, JsonObject map, Handler<AsyncResult<ApiResult>> handler);

    void create(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler);

    void create(KubernetesDatabase db, ApiBodyRequest request, Handler<AsyncResult<ApiResult>> handler);

    void delete(KubernetesDatabase db, long id, Handler<AsyncResult<ApiResult>> handler);

    void delete(KubernetesDatabase db, ApiBodyRequest request, Handler<AsyncResult<ApiResult>> handler);

    void update(KubernetesDatabase db, long id, MultiMap map, Handler<AsyncResult<ApiResult>> handler);

    void update(KubernetesDatabase db, long id, ApiBodyRequest request, Handler<AsyncResult<ApiResult>> handler);

    void buildTable(KubernetesDatabase db, Handler<AsyncResult<Void>> handler);

    void checkTable(KubernetesDatabase db, Handler<AsyncResult<Boolean>> handler);

}
