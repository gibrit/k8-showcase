/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.sstore.LocalSessionStore;
import java.util.HashSet;
import java.util.Set;
import org.saltuk.core.KubernetesManager;
import org.saltuk.core.StringUtils;
import org.saltuk.core.db.DbTable;
import org.saltuk.core.db.query.DbQuery;
import org.saltuk.core.db.query.live.DbLiveQueryHandler;
import org.saltuk.core.types.KubernetesDatabase;

/**
 *
 * @author saltuk
 */
public class ApiRouterImpl implements ApiRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouterImpl.class.getName());
    private final DbTable table;
    private final Vertx vertx;
    private final String serviceName;
    private final DbQuery dbQuery;
    private boolean builded;

    public ApiRouterImpl(Vertx vertx, String serviceName, DbTable table) {
        this.table = table;
        this.serviceName = serviceName;
        this.dbQuery = DbQuery.create(vertx, table);
        this.vertx = vertx;

    }

    @Override
    public Router router() {
        final Router router = Router.router(vertx);
        String path = "/api/" + this.table.name();
        if ("true".equals(System.getProperty("KUBERNETES_SERVICE_DEBUG", ""))) {
            Set<HttpMethod> methods = new HashSet<>();
            methods.add(HttpMethod.GET);

            methods.add(HttpMethod.POST);
            methods.add(HttpMethod.DELETE);
            methods.add(HttpMethod.PUT);
            router.route().handler(CorsHandler.create(".*").allowedMethods(methods).allowedHeader("content-type"));
        }
        router.route().handler(TimeoutHandler.create(2000, 500));
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.mountSubRouter(path + "/eventbus", DbLiveQueryHandler.create(vertx).handler(new SockJSHandlerOptions()));

        router.route().method(HttpMethod.GET).path(path + "/live").handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    MultiMap request = context.request().params();
                    this.dbQuery.selectLive(database, request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });

        router.route().method(HttpMethod.POST).consumes("application/json").path(path).handler(context -> {

            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    ApiBodyRequest request = ApiBodyRequest.create(context.getBodyAsJson());
                    this.dbQuery.create(database, request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.PUT).consumes("application/json").path(StringUtils.append(path, "/:id")).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    ApiBodyRequest request = ApiBodyRequest.create(context.getBodyAsJson());
                    this.dbQuery.update(database, Long.valueOf(context.pathParam("id")), request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.DELETE).consumes("application/json").path(path).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    ApiBodyRequest request = ApiBodyRequest.create(context.getBodyAsJson());
                    this.dbQuery.delete(database, request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.DELETE).consumes("application/json").path(StringUtils.append(path, "/:id")).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    ApiBodyRequest request = ApiBodyRequest.create(context.getBodyAsJson());

                    this.dbQuery.delete(database, Long.valueOf(context.pathParam("id")), executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.PUT).path(StringUtils.append(path, "/:id")).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    MultiMap request = context.request().formAttributes();
                    this.dbQuery.update(database, Long.valueOf(context.pathParam("id")), request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.POST).path(path).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    MultiMap request = context.request().formAttributes();
                    this.dbQuery.create(database, request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.GET).path(StringUtils.append(path, "/:id")).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    MultiMap request = context.request().params();
                    this.dbQuery.select(database, Long.valueOf(context.pathParam("id")), request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());
                }
            });
        });
        router.route().method(HttpMethod.GET).path(path).handler(context -> {
            this.db(readDb -> {
                if (readDb.succeeded()) {
                    final KubernetesDatabase database = readDb.result();
                    MultiMap request = context.request().params();
                    this.dbQuery.select(database, request, executeQuery -> {
                        if (executeQuery.succeeded()) {
                            final ApiResult result = executeQuery.result();
                            context.response().putHeader("content-type", "application/json").setStatusCode(result.statusCode()).end(result.asJson().toBuffer());
                        } else {
                            context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, executeQuery.cause().getMessage()).asJson().toBuffer());
                            this.dbQuery.checkTable(database, execute -> {
                                if (execute.succeeded()) {
                                    this.builded = execute.result();
                                }
                            });
                        }
                    });
                } else {
                    context.response().putHeader("content-type", "application/json").setStatusCode(500).end(ApiResult.fail(500, readDb.cause().getMessage()).asJson().toBuffer());

                }
            });

        });
        return router;
    }

    private void db(Handler<AsyncResult<KubernetesDatabase>> handler) {

        KubernetesManager.newInstance().onDatabaseDown(v -> {
            LOGGER.info("Database down for " + this.serviceName + " setting check for upload dbs");
            this.builded = false;
        }).database(serviceName, readDb -> {
            if (readDb.succeeded()) {
                final KubernetesDatabase database = readDb.result();

                if (!builded) {
                    this.dbQuery.checkTable(database, checkTable -> {
                        if (checkTable.succeeded()) {
                            if (!checkTable.result()) {
                                this.dbQuery.buildTable(database, execute -> {

                                    if (execute.succeeded()) {
                                        this.builded = true;
                                        handler.handle(Future.succeededFuture(database));
                                    } else {
                                        handler.handle(Future.failedFuture(execute.cause()));
                                    }
                                });
                            } else {
                                  this.builded = true;
                                handler.handle(Future.succeededFuture(database));
                            }
                        } else {
                            handler.handle(Future.failedFuture(checkTable.cause()));
                        }
                    });
                } else {
                    handler.handle(Future.succeededFuture(database));
                }
            } else {
                handler.handle(Future.failedFuture(readDb.cause()));
            }
        });
    }
}
