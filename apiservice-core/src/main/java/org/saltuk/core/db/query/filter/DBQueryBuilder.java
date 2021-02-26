/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import java.util.Set;
import org.saltuk.core.api.ApiPagination;
import org.saltuk.core.db.DbCommand;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * DB Query Builder
 *
 * @author saltuk
 */
public interface DBQueryBuilder {

    static DBQueryBuilder create(KubernetesDBType type, DbTable table) {

        return new DBQueryBuilderImpl(table, type);
    }

    int index();

    /**
     * Query Sql
     *
     * @param pagination
     * @return
     */
    String sql(ApiPagination pagination);

    /**
     * Count Query
     *
     * @return
     */
    String countSql();

    Tuple tuple();

    DBQueryBuilder filder(DBFilter value);

    DBQueryBuilder filters(Set<DBFilter> filters);

    DbCommand update(long id, MultiMap map);

    DbCommand update(long id, JsonObject map);

    DbCommand create(MultiMap map);

    DbCommand create(JsonObject map);

    DbCommand create(JsonArray map);

    DbCommand delete(long id);

    DbCommand delete(JsonArray map);

    DbCommand delete(JsonObject map);

}
