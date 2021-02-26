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
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.saltuk.core.StringUtils;
import org.saltuk.core.api.ApiPagination;
import org.saltuk.core.db.DbCommand;
import org.saltuk.core.db.DbCommandQueryBuilder;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * @author saltuk
 */
public class DBQueryBuilderImpl implements DBQueryBuilder {

    private int index;
    private StringBuilder sql;
    private Tuple tuple;
    private final String paramName;
    private final KubernetesDBType dbType;
    private final StringBuilder countSql;
    private final DbTable table;

    public DBQueryBuilderImpl(DbTable table, KubernetesDBType type) {
        this.table = table;
        this.sql = new StringBuilder().append(table.selectQuery());
        this.countSql = new StringBuilder().append(table.countQuery());
        this.tuple = Tuple.tuple();
        this.index = 0;
        this.paramName = type.paramName();
        this.dbType = type;
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public Tuple tuple() {
        return this.tuple;
    }

    @Override
    public DBQueryBuilder filters(Set<DBFilter> filters) {
        if (filters.size() > 0) {

            filters.forEach(v -> {
                this.filder(v);
            });
        }
        return this;
    }

    @Override
    public DBQueryBuilder filder(DBFilter value) {
        if (value != null) {
            if (index == 0) {

                this.append(" where ");
            } else {
                this.append(" and ");
            }
            this.append(value.name());
            switch (value.filter()) {
                case EQUALS: {
                    this.append("=");
                    this.preparaParams(value);
                    break;
                }
                case IN: {

                    this.append(" in (");
                    this.preparaParams(value);
                    this.append(")");
                    break;
                }
                case LIKE: {
                    this.append(" like ");
                    this.preparaParams(value);
                    break;
                }
                case MAX: {
                    this.append(" <");
                    this.preparaParams(value);
                    break;
                }
                case MIN: {

                    this.append(" >");
                    this.preparaParams(value);
                    break;
                }
                case NOT: {

                    this.append(" !=");
                    this.preparaParams(value);
                    break;
                }
                case NOT_IN: {
                    this.append(" not in (");
                    this.preparaParams(value);
                    this.append(")");
                    break;
                }
                case NOT_LIKE: {
                    this.append(" not like  (");
                    this.preparaParams(value);
                    this.append(")");
                    break;
                }
            }

        }
        return this;
    }

    private String parapaneName(int index) {
        return this.dbType == KubernetesDBType.MYSQL ? "?" : StringUtils.append("$", index);
    }

    private void preparaParams(DBFilter val) {
        switch (this.dbType) {
            case POSTGRESQL: {
                if (val.isArray()) {
                    final List vals = val.values();
                    IntStream.range(0, vals.size()).forEach(v -> {

                        if (v > 0) {
                            this.append(",");
                        }
                        this.tuple.addValue(vals.get(v));
                        this.index++;
                        this.append("$");
                        this.append(this.index);
                    });
                } else {
                    this.index++;
                    this.tuple.addValue(val.value());
                    this.append("$");
                    this.append(this.index);
                }
                break;
            }
            case MYSQL: {
                if (val.isArray()) {
                    final List vals = val.values();
                    IntStream.range(0, vals.size()).forEach(v -> {

                        if (v > 0) {
                            this.append(",");
                        }
                        this.tuple.addValue(vals.get(v));
                        this.index++;
                        this.append("?");
                        this.append(this.index);
                    });
                } else {
                    this.index++;
                    this.tuple.addValue(val.value());
                    this.append("?");
                }
                break;
            }

        }
    }

    @Override
    public String sql(ApiPagination pagination) {
        this.prepareteLimit(pagination);
        return this.sql.toString();
    }

    @Override
    public String countSql() {
        return this.countSql.toString();
    }

    private void append(Object val) {
        this.sql.append(val);
        this.countSql.append(val);
    }

    private void prepareteLimit(ApiPagination pagination) {
        switch (this.dbType) {
            case MYSQL: {
                this.sql.append(" limit  ? ,  ? ");
                this.tuple.addValue(pagination.offset()).addValue(pagination.limit());
                break;
            }
            case POSTGRESQL: {
                this.sql.append(" limit  ");
                this.index++;
                this.sql.append(paramName).append(this.index);
                this.tuple.addValue(pagination.limit());
                if (pagination.offset() > 0) {
                    this.index++;
                    this.sql.append("  offset ").append(paramName).append(this.index);
                    this.tuple.addValue(pagination.offset());
                }
                break;
            }
        }
    }

    @Override
    public DbCommand update(long id, MultiMap map) {
        return DbCommandQueryBuilder.update(dbType, table, id).prepareQuery(map);
    }

    @Override
    public DbCommand create(MultiMap map) {
        return DbCommandQueryBuilder.create(dbType, table).prepareQuery(map);
    }

    @Override
    public DbCommand delete(long id) {
        return DbCommandQueryBuilder.delete(dbType, table, id).prepareQuery();
    }

    @Override
    public DbCommand update(long id, JsonObject map) {
        return DbCommandQueryBuilder.update(dbType, table, id).prepareQuery(map);
    }

    @Override
    public DbCommand create(JsonObject map) {
        return DbCommandQueryBuilder.create(dbType, table).prepareQuery(map);
    }

    @Override
    public DbCommand create(JsonArray map) {
        return DbCommandQueryBuilder.create(dbType, table).prepareQuery(map);
    }

    @Override
    public DbCommand delete(JsonArray map) {
        return DbCommandQueryBuilder.delete(dbType, table).prepareQuery(map);
    }

    @Override
    public DbCommand delete(JsonObject map) {
        return DbCommandQueryBuilder.delete(dbType, table).prepareQuery(map);
    }

}
