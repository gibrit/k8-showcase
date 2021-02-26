/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.saltuk.core.types.KubernetesDBType;

/**
 * Database Command Builder
 *
 * @author saltuk
 */
public interface DbCommandQueryBuilder {

    static DbCommandQueryBuilder create(KubernetesDBType type, DbTable table) {
        return new DbCommandQueryBuilderImpl(type, DbCommandQueryType.CREATE, table);
    }

    static DbCommandQueryBuilder update(KubernetesDBType type, DbTable table, long id) {
        return new DbCommandQueryBuilderImpl(type, DbCommandQueryType.UPDATE, table, id);
    }

    static DbCommandQueryBuilder delete(KubernetesDBType type, DbTable table, long id) {
        return new DbCommandQueryBuilderImpl(type, DbCommandQueryType.DELETE, table, id);
    }

    static DbCommandQueryBuilder delete(KubernetesDBType type, DbTable table) {
        return new DbCommandQueryBuilderImpl(type, DbCommandQueryType.DELETE, table);
    }

    DbCommand prepareQuery();

    DbCommand prepareQuery(MultiMap map);

    DbCommand prepareQuery(JsonObject map);

    DbCommand prepareQuery(JsonArray map);

}
