/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.sqlclient.Tuple;
import java.util.HashSet;
import java.util.Set;
import org.saltuk.core.api.ApiBodyRequest;
import org.saltuk.core.api.ApiPagination;
import org.saltuk.core.api.ApiResult;
import org.saltuk.core.api.ApiRouterImpl;
import org.saltuk.core.db.DbCommand;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.db.query.filter.DBFieldFilter;
import org.saltuk.core.db.query.filter.DBFilter;
import org.saltuk.core.db.query.filter.DBFilterGroup;
import org.saltuk.core.db.query.filter.DBQueryBuilder;
import org.saltuk.core.db.query.live.DbLiveQuery;
import org.saltuk.core.db.query.row.DbRowSet;
import org.saltuk.core.types.KubernetesDBType;
import org.saltuk.core.types.KubernetesDatabase;

/**
 *
 * @author saltuk
 */
public class DbQueryImpl implements DbQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouterImpl.class.getName());
    private final DbTable table;
    private final Vertx vertx;

    public DbQueryImpl(Vertx vertx, DbTable table) {
        this.table = table;
        this.vertx = vertx;
    }

    @Override
    public DbTable table() {
        return this.table;
    }

    @Override
    public void select(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        this.select(db, -1L, map, handler);
    }

    @Override
    public void select(KubernetesDatabase db, long id, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        this.select(db, id, false, map, handler);
    }

    @Override
    public void create(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        DbCommand create = DBQueryBuilder.create(db.type(), table).create(map);
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.created(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    @Override
    public void create(KubernetesDatabase db, ApiBodyRequest request, Handler<AsyncResult<ApiResult>> handler) {

        DbCommand create = request.multiple() ? DBQueryBuilder.create(db.type(), table).create(request.payload().toJsonArray()) : DBQueryBuilder.create(db.type(), table).create(request.payload().toJsonObject());
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.created(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    @Override
    public void delete(KubernetesDatabase db, long id, Handler<AsyncResult<ApiResult>> handler) {
        DbCommand create = DBQueryBuilder.create(db.type(), table).delete(id);
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.deleted(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    @Override
    public void delete(KubernetesDatabase db, ApiBodyRequest value, Handler<AsyncResult<ApiResult>> handler) {
        DbCommand create = value.multiple() ? DBQueryBuilder.create(db.type(), table).delete(value.payload().toJsonArray()) : DBQueryBuilder.create(db.type(), table).delete(value.payload().toJsonObject());
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.deleted(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    @Override
    public void update(KubernetesDatabase db, long id, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        DbCommand create = DBQueryBuilder.create(db.type(), table).update(id, map);
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.update(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    @Override
    public void update(KubernetesDatabase db, long id, ApiBodyRequest map, Handler<AsyncResult<ApiResult>> handler) {
        DbCommand create = DBQueryBuilder.create(db.type(), table).update(id, map.payload().toJsonObject());
        if (create.valid()) {
            this.runCommandWithReturn(db, create, execute -> {
                if (execute.succeeded()) {
                    handler.handle(Future.succeededFuture(ApiResult.update(execute.result())));
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                }
            });
        } else {
            handler.handle(Future.succeededFuture(ApiResult.fail(400, create.errors())));
        }
    }

    private void countRow(KubernetesDatabase db, String sql, Tuple tuple, Handler<AsyncResult<Long>> handler) {
        this.query(db, sql, tuple, readCount -> {
            if (readCount.succeeded()) {
                final Long total = readCount.result().getJsonObject(0).getLong("total", 0L);
                handler.handle(Future.succeededFuture(total));
            } else {
                handler.handle(Future.failedFuture(readCount.cause()));
            }
        });
    }

    private void query(KubernetesDatabase db, String sql, Tuple tuple, Handler<AsyncResult<JsonArray>> handler) {
        switch (db.type()) {
            case POSTGRESQL: {
                db.postgresql().preparedQuery(sql).execute(tuple, runCommand -> {
                    if (runCommand.succeeded()) {
                        DbRowSet create = DbRowSet.create(runCommand.result());
                        create.prepareRow(handler);
                    } else {
                        LOGGER.error("Query Error", runCommand.cause());
                        handler.handle(Future.failedFuture("QUERY_ERROR"));
                    }
                });
                break;
            }
            default: {
                handler.handle(Future.failedFuture("Database Not Supported for now"));
                break;
            }
        }
    }

    private void execute(KubernetesDatabase db, String sql, Handler<AsyncResult<Void>> handler) {
        switch (db.type()) {
            case POSTGRESQL: {
                db.postgresql().query(sql).execute(runCommand -> {
                    if (runCommand.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error("Query Error", runCommand.cause());
                        handler.handle(Future.failedFuture(new Throwable("QUERY_ERROR", runCommand.cause())));
                    }
                });
                break;
            }
            default: {
                handler.handle(Future.failedFuture("Database Not Supported for now"));
                break;
            }
        }
    }

    @Override
    public void checkTable(KubernetesDatabase db, Handler<AsyncResult<Boolean>> handler) {
        this.query(db, "SELECT count(*) as total FROM information_schema.tables WHERE table_name=$1  AND table_schema=$2", Tuple.of(table.name(), "public"), execute -> {
            if (execute.succeeded()) {
                handler.handle(Future.succeededFuture(execute.result().getJsonObject(0).getLong("total") > 0));
            } else {
                handler.handle(Future.failedFuture(execute.cause()));
            }
        });
    }

    private void runCommand(KubernetesDatabase db, DbCommand cmd, Handler<AsyncResult<Void>> handler) {

        switch (db.type()) {
            case POSTGRESQL: {
                this.query(db, cmd.sql(), cmd.tuple(), doExecute -> {
                    if (doExecute.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(Future.failedFuture(doExecute.cause()));
                        LOGGER.error("Query Error", doExecute.cause());
                    }
                });
                break;
            }
            default: {
                handler.handle(Future.failedFuture("Database not Supported"));
                break;
            }
        }

    }

    private void runCommandWithReturn(KubernetesDatabase db, DbCommand cmd, Handler<AsyncResult<JsonArray>> handler) {

        switch (db.type()) {
            case POSTGRESQL: {
                this.query(db, cmd.sql(), cmd.tuple(), doExecute -> {
                    if (doExecute.succeeded()) {
                        handler.handle(Future.succeededFuture(doExecute.result()));
                    } else {
                        handler.handle(Future.failedFuture(doExecute.cause()));
                    }
                });
                break;
            }
            default: {
                handler.handle(Future.failedFuture("Database not Supported"));
                break;
            }
        }

    }

    @Override
    public void buildTable(KubernetesDatabase db, Handler<AsyncResult<Void>> handler) {
        if (db.type() == KubernetesDBType.POSTGRESQL) {

            String dbLiveTrigger = "create  or replace function db_notify_live () RETURNS  trigger \n"
                    + " LANGUAGE 'plpgsql' \n"
                    + " IMMUTABLE\n"
                    + "  COST 100 \n"
                    + " SET search_path=admin, pg_temp, public \n"
                    + "as $BODY$ \n"
                    + " BEGIN perform pg_notify('pg_db_live_db', TG_TABLE_NAME); \n"
                    + "return null;\n"
                    + "END; $BODY$\n";
            this.execute(db, dbLiveTrigger, execute -> {
                if (execute.succeeded()) {
                    this.execute(db, table.query(db.type()), handler);
                } else {
                    handler.handle(Future.failedFuture(execute.cause()));
                }
            });
        } else {
            this.execute(db, table.query(db.type()), handler);
        }
    }

    @Override
    public void select(KubernetesDatabase db, JsonObject map, Handler<AsyncResult<ApiResult>> handler) {
        this.select(db, -1L, map, handler);
    }

    @Override
    public void select(KubernetesDatabase db, long id, JsonObject map, Handler<AsyncResult<ApiResult>> handler) {
        final Set<DBFilter> filters = new HashSet<>();
        ApiPagination pagination = ApiPagination.create(map);
        if (id > 0) {
            map.put(table.primaryField().name(), String.valueOf(id));
        }
        if (map != null) {

            this.table.fields().forEach(v -> {
                filters.addAll(DBFieldFilter.create(v).prepare(map));
            });
        }
        DBQueryBuilder query = DBQueryBuilder.create(db.type(), table).filters(filters);
        this.countRow(db, query.countSql(), query.tuple(), countQuery -> {
            if (countQuery.succeeded()) {
                final long result = countQuery.result();
                if (result > 0) {
                    this.query(db, query.sql(pagination), query.tuple(), readQuery -> {
                        if (readQuery.succeeded()) {
                            handler.handle(Future.succeededFuture(ApiResult.succcess(result, readQuery.result(), pagination)));
                        } else {
                            handler.handle(Future.succeededFuture(ApiResult.fail(500, readQuery.cause().getMessage())));
                        }
                    });
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.succcess(0L, new JsonArray(), pagination)));
                }
            } else {
                handler.handle(Future.succeededFuture(ApiResult.fail(500, countQuery.cause().getMessage())));
            }
        });

    }

    @Override
    public void selectLive(KubernetesDatabase db, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        this.select(db, -1L, true, map, handler);
    }

    private void select(KubernetesDatabase db, long id, boolean live, MultiMap map, Handler<AsyncResult<ApiResult>> handler) {
        final Set<DBFilter> filters = new HashSet<>();
        ApiPagination pagination = ApiPagination.create(map);
        if (id > 0) {
            map.add(table.primaryField().name(), String.valueOf(id));
        }
        if (map != null) {

            this.table.fields().forEach(v -> {
                filters.addAll(DBFieldFilter.create(v).prepare(map));
            });
        }
        DBQueryBuilder query = DBQueryBuilder.create(db.type(), table).filters(filters);

        this.countRow(db, query.countSql(), query.tuple(), countQuery -> {
            if (countQuery.succeeded()) {
                final long result = countQuery.result();
                if (result > 0) {
                    this.query(db, query.sql(pagination), query.tuple(), readQuery -> {
                        if (readQuery.succeeded()) {
                            if (live) {
                                DBFilterGroup filterGroup = DBFilterGroup.create(filters, pagination);
                                final DbLiveQuery liveQuery = DbLiveQuery.create(vertx, db, table, filterGroup);
                                liveQuery.start(doStart -> {
                                    if (doStart.succeeded()) {
                                        liveQuery.connect(null, execute -> {
                                            if (execute.succeeded()) {
                                                handler.handle(Future.succeededFuture(ApiResult.succcess(result, readQuery.result(), pagination).liveToken(execute.result())));
                                            } else {
                                                handler.handle(Future.succeededFuture(ApiResult.fail(500, execute.cause().getMessage())));
                                            }
                                        });
                                    } else {
                                        handler.handle(Future.succeededFuture(ApiResult.fail(500, "Error While Staring Live Query")));
                                    }
                                });
                            } else {
                                handler.handle(Future.succeededFuture(ApiResult.succcess(result, readQuery.result(), pagination)));
                            }
                        } else {
                            handler.handle(Future.succeededFuture(ApiResult.fail(500, readQuery.cause().getMessage())));
                        }
                    });
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.succcess(0L, new JsonArray(), pagination)));
                }
            } else {
                handler.handle(Future.succeededFuture(ApiResult.fail(500, countQuery.cause().getMessage())));
            }
        });
    }

    @Override
    public void select(KubernetesDatabase db, DBFilterGroup filter, Handler<AsyncResult<ApiResult>> handler) {
        DBQueryBuilder query = DBQueryBuilder.create(db.type(), table).filters(filter.filters());

        this.countRow(db, query.countSql(), query.tuple(), countQuery -> {
            if (countQuery.succeeded()) {
                final long result = countQuery.result();
                if (result > 0) {
                    this.query(db, query.sql(filter.pagination()), query.tuple(), readQuery -> {
                        if (readQuery.succeeded()) {
                            handler.handle(Future.succeededFuture(ApiResult.succcess(result, readQuery.result(), filter.pagination())));
                        } else {
                            handler.handle(Future.succeededFuture(ApiResult.fail(500, readQuery.cause().getMessage())));
                        }
                    });
                } else {
                    handler.handle(Future.succeededFuture(ApiResult.succcess(0L, new JsonArray(), filter.pagination())));
                }
            } else {
                handler.handle(Future.succeededFuture(ApiResult.fail(500, countQuery.cause().getMessage())));
            }
        });
    }

}
