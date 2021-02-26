/*
 * Copyright 2020 saltuk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.saltuk.core;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.pubsub.PgSubscriber;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.RedisRole;
import io.vertx.redis.client.RedisSlaves;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.kubernetes.KubernetesServiceImporter;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.sqlclient.PoolOptions;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.saltuk.core.types.KubernetesDBType;
import static org.saltuk.core.types.KubernetesDBType.POSTGRESQL;
import static org.saltuk.core.types.KubernetesDBType.REDIS;
import org.saltuk.core.types.KubernetesDatabase;
import org.saltuk.core.types.KubernetesDatabaseConfig;
import org.saltuk.core.types.KubernetesLiveDatabase;
import org.saltuk.core.types.KubernetesModuleConfig;
import org.saltuk.core.types.KubernetesRecord;
import org.saltuk.core.types.KubernetesRecordLocation;
import org.saltuk.core.types.KubernetesSecret;

/**
 * K8 Manager
 *
 * @author Saltık Buğra Avcı ben@saltuk.org
 */
class KubernetesManagerImpl implements KubernetesManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesManagerImpl.class.getName());
    private static KubernetesManager instance;

    public static synchronized KubernetesManager newInstance() {
        if (instance == null) {
            instance = new KubernetesManagerImpl();
        }
        return instance;
    }

    private Vertx vertx;
    private ServiceDiscovery discoveryService;
    private final Map<String, KubernetesRecord> services;
    private final Map<String, KubernetesDatabase> databases;
    private final Map<String, String> serviceUpdater;
    private final Map<String, String> configUpdater;
    private Handler<Void> databaseDownHandler;

    private KubernetesManagerImpl() {
        this.services = new ConcurrentHashMap<>();
        this.databases = new ConcurrentHashMap<>();
        this.serviceUpdater = new ConcurrentHashMap<>();
        this.configUpdater = new ConcurrentHashMap<>();
    }

    @Override
    public void database(String serviceName, Handler<AsyncResult<KubernetesDatabase>> handler) {
        final KubernetesDatabase pool = this.databases.get(serviceName);
        if (pool != null) {
            handler.handle(Future.succeededFuture(pool));
        } else {
            this.service(serviceName, readModule -> {
                if (readModule.succeeded()) {
                    this.configUpdater.put(serviceName + "-config", serviceName);
                    final KubernetesRecord moduleConfig = readModule.result();
                    final String dbService = moduleConfig.config().dbService();
                    final KubernetesDBType dbType = moduleConfig.config().dbType();
                    this.dbConfig(dbType != KubernetesDBType.REDIS, StringUtils.append(dbService, "-config"), readConf -> {
                        if (readConf.succeeded()) {
                            this.configUpdater.put(StringUtils.append(dbService, "-config"), serviceName);
                            KubernetesDatabaseConfig dbConfig = readConf.result();
                            this.discovery(readDiscovery -> {
                                if (readDiscovery.succeeded()) {
                                    ServiceDiscovery discovery = readDiscovery.result();
                                    discovery.getRecord((Record t) -> t.getName().equals(dbService), readService -> {
                                        if (readService.succeeded()) {
                                            Record result = readService.result();
                                            if (result != null && result.getStatus() == Status.UP) {
                                                this.serviceUpdater.put(dbService, serviceName);
                                                KubernetesRecordLocation loc = KubernetesRecordLocation.create(result.getLocation());
                                                KubernetesDatabase client = this.createClient(dbType, dbConfig, loc);
                                                if (client != null) {
                                                    this.databases.put(serviceName, client);
                                                    handler.handle(Future.succeededFuture(client));
                                                } else {
                                                    handler.handle(Future.failedFuture("No Database Service"));
                                                }

                                            } else {
                                                handler.handle(Future.failedFuture(StringUtils.append(serviceName, " kubernet service not found or not running")));
                                            }
                                        } else {
                                            handler.handle(Future.failedFuture(readService.cause()));
                                        }
                                    });
                                } else {
                                    handler.handle(Future.failedFuture(readDiscovery.cause()));
                                }
                            });
                        } else {
                            handler.handle(Future.failedFuture(readConf.cause()));
                        }
                    });
                } else {
                    handler.handle(Future.failedFuture(StringUtils.append(serviceName, " kubernetes  module not found ")));
                }
            });
        }

    }

    @Override
    public void vertx(Handler<AsyncResult<Vertx>> handler) {

        if (vertx != null && vertx.isClustered()) {

            handler.handle(Future.succeededFuture(vertx));
        } else {
            try {
                final String host = InetAddress.getLocalHost().getHostAddress();
                ClusterManager mgr = new HazelcastClusterManager(ClusterConfig.hazelcast().config());
                VertxOptions options = new VertxOptions().setClusterManager(mgr).setEventBusOptions(new EventBusOptions().setPort(1071).setHost(host).setClustered(true));
                Vertx.clusteredVertx(options, readVertex -> {
                    if (readVertex.succeeded()) {
                        vertx = readVertex.result();
                        handler.handle(Future.succeededFuture(vertx));
                    } else {
                        handler.handle(Future.failedFuture(readVertex.cause()));
                    }
                });
            } catch (UnknownHostException ex) {
                handler.handle(Future.failedFuture(ex));
            }
        }

    }

    @Override
    public void config(String namespace, String name, Handler<AsyncResult<JsonObject>> handler) {
        this.readData(namespace, name, false, handler);
    }

    @Override
    public void config(String name, Handler<AsyncResult<JsonObject>> handler) {
        this.config(null, name, handler);
    }

    @Override
    public void configs(Set<String> serviceNames, Handler<AsyncResult<JsonArray>> handler) {
        this.configs(null, serviceNames, handler);
    }

    @Override
    public void configs(String namespace, Set<String> serviceNames, Handler<AsyncResult<JsonArray>> handler) {
        this.readDatas(namespace, serviceNames, false, handler);
    }

    @Override
    public void secret(String name, Handler<AsyncResult<KubernetesSecret>> handler) {
        this.secret(null, name, handler);
    }

    @Override
    public void secret(String namespace, String serviceName, Handler<AsyncResult<KubernetesSecret>> handler) {
        this.readData(namespace, serviceName, true, read -> {
            if (read.succeeded()) {
                handler.handle(Future.succeededFuture(KubernetesSecret.create(read.result())));
            } else {
                handler.handle(Future.failedFuture(read.cause()));
            }
        });
    }

    @Override
    public void moduleConfig(String name, Handler<AsyncResult<KubernetesModuleConfig>> handler) {
        this.moduleConfig(null, name, handler);
    }

    @Override
    public void moduleConfig(String namespace, String name, Handler<AsyncResult<KubernetesModuleConfig>> handler) {
        this.config(namespace, StringUtils.append(name, "-config"), readConf -> {
            if (readConf.succeeded()) {
                if (readConf.result().isEmpty()) {
                    handler.handle(Future.failedFuture(StringUtils.append(name, "  module  configuration is not defined")));
                } else {
                    KubernetesModuleConfig conf = KubernetesModuleConfig.create(readConf.result());
                    handler.handle(Future.succeededFuture(conf));
                }
            } else {
                handler.handle(Future.failedFuture(new Throwable(StringUtils.append(name, " module configuration cannot be read"), readConf.cause())));
            }
        });
    }

    private void dbConfig(boolean secret, String name, Handler<AsyncResult<KubernetesDatabaseConfig>> handler) {
        if (secret) {
            this.secret(name, readSecret -> {
                if (readSecret.succeeded()) {
                    KubernetesSecret result = readSecret.result();
                    handler.handle(Future.succeededFuture(KubernetesDatabaseConfig.create().secretConfig(result)));
                } else {
                    handler.handle(Future.failedFuture(readSecret.cause()));
                }
            });
        } else {
            this.config(name, readConfig -> {
                if (readConfig.succeeded()) {
                    handler.handle(Future.succeededFuture(KubernetesDatabaseConfig.create().config(readConfig.result())));
                } else {
                    handler.handle(Future.failedFuture(readConfig.cause()));
                }
            });
        }
    }

    private void readData(String namespace, String name, boolean secret, Handler<AsyncResult<JsonObject>> handler) {
        this.vertx(readVertx -> {
            if (readVertx.succeeded()) {

                if (StringUtils.isEmpty(name)) {
                    handler.handle(Future.failedFuture(StringUtils.append("ConfigMap name is  empty")));
                    return;
                }
                JsonObject config = new JsonObject();
                if (!StringUtils.isEmpty(namespace)) {
                    config.put("namespace", namespace);
                }
                config
                        .put("name", name)
                        .put("secret", secret);

                final String token = System.getProperty("KUBERNETES_SERVICE_TOKEN", "");
                final String host = System.getProperty("KUBERNETES_SERVICE_HOST");
                final boolean ssl = Boolean.valueOf(System.getProperty("KUBERNETES_SERVICE_SSL", "true"));
                final int port = Integer.valueOf(System.getProperty("KUBERNETES_SERVICE_PORT", "0"));
                if (!StringUtils.isEmpty(token)) {
                    config.put("token", token);
                }
                if (!StringUtils.isEmpty(host)) {
                    config.put("host", host);
                }
                if (port > 0) {
                    config.put("port", port);
                }
                config.put("ssl", ssl);
                ConfigStoreOptions store = new ConfigStoreOptions()
                        .setType("configmap")
                        .setConfig(config);

                ConfigRetriever retriever = ConfigRetriever.create(readVertx.result(), new ConfigRetrieverOptions().addStore(store));
                retriever.getConfig(execute -> {
                    if (execute.succeeded()) {
                        handler.handle(Future.succeededFuture(execute.result()));
                    } else {
                        handler.handle(Future.failedFuture(execute.cause()));
                    }
                });
            } else {
                handler.handle(Future.failedFuture(readVertx.cause()));
            }
        });
    }

    private void readDatas(String namespace, Set<String> names, boolean secret, Handler<AsyncResult<JsonArray>> handler) {
        if (names != null && !names.isEmpty()) {
            this.vertx(readVertx -> {
                if (readVertx.succeeded()) {
                    final List<Future> futures = new ArrayList();
                    names.forEach(v -> {
                        Future<JsonObject> future = Future.future(promise -> this.readData(namespace, v, secret, promise));
                        futures.add(future);
                    });

                    CompositeFuture.all(futures).onComplete(ar -> {
                        if (ar.succeeded()) {
                            final JsonArray result = new JsonArray();
                            ar.result().list().forEach(v -> {
                                result.add(v);
                            });
                            handler.handle(Future.succeededFuture(result));
                        } else {
                            handler.handle(Future.failedFuture(ar.cause()));
                        }
                    });

                } else {
                    handler.handle(Future.failedFuture(readVertx.cause()));
                }
            });
        } else {
            handler.handle(Future.failedFuture("Service Names are Empty"));
        }
    }

    @Override
    public void service(String name, Handler<AsyncResult<KubernetesRecord>> handler) {
        this.service(null, name, handler);
    }

    @Override
    public void service(String nameservice, Set<String> names, Handler<AsyncResult<Set<KubernetesRecord>>> handler) {
        this.vertx(readVertx -> {
            if (readVertx.succeeded()) {
                final List<Future> futures = new ArrayList();
                names.forEach(name -> {
                    Future<KubernetesRecord> future = Future.future(promise -> this.service(nameservice, name, promise));
                    futures.add(future);
                });
                CompositeFuture.all(futures).onComplete(execute -> {
                    if (execute.succeeded()) {
                        CompositeFuture result = execute.result();
                        handler.handle(Future.succeededFuture(new HashSet<>(result.list())));
                    } else {
                        handler.handle(Future.failedFuture(new Throwable("Error While Reading Services", execute.cause())));
                    }
                });
            } else {
                handler.handle(Future.failedFuture(readVertx.cause()));
            }
        });
    }

    @Override
    public void service(Set<String> names, Handler<AsyncResult<Set<KubernetesRecord>>> handler) {
        this.service(null, names, handler);
    }

    @Override
    public void service(String nameservice, String name, Handler<AsyncResult<KubernetesRecord>> handler) {

        this.moduleConfig(name, readConf -> {
            if (readConf.succeeded()) {
                this.discovery(readDiscovery -> {
                    if (readDiscovery.succeeded()) {

                        ServiceDiscovery discovery = readDiscovery.result();

                        discovery.getRecord((Record t) -> t.getName().equals(name), readService -> {
                            if (readService.succeeded()) {
                                Record result = readService.result();
                                if (result != null && result.getStatus() == Status.UP) {
                                    KubernetesRecord readedRecord = KubernetesRecord.create(result, readConf.result());
                                    this.services.put(readedRecord.accessName(), readedRecord);
                                    handler.handle(Future.succeededFuture(readedRecord));
                                } else {
                                    handler.handle(Future.failedFuture(StringUtils.append(name, " kubernet service not found or status is not up")));
                                }
                            } else {
                                handler.handle(Future.failedFuture(readService.cause()));
                            }
                        });
                    } else {
                        handler.handle(Future.failedFuture(readDiscovery.cause()));
                    }
                });
            } else {
                handler.handle(Future.failedFuture(readConf.cause()));
            }
        });

    }

    private void discovery(Handler<AsyncResult<ServiceDiscovery>> handler) {
        this.vertx(readVertx -> {
            if (readVertx.succeeded()) {
                if (this.discoveryService == null) {
                    this.discoveryService = ServiceDiscovery.create(readVertx.result());
                    vertx.eventBus().consumer(discoveryService.options().getAnnounceAddress(), v -> {
                        JsonObject d = (JsonObject) v.body();
                        final Record record = new Record(d);
                        LOGGER.info("Kubernetes Event:" +record.toJson().encodePrettily());
                        String service = this.serviceUpdater.get(record.getName());
                        if (service != null) {
                            this.databases.remove(service);
                            LOGGER.info("Database service" + service + " removed from cache   cause : " + record.getName() + " service is changed as  " + record.getStatus().name());
                            if (this.databaseDownHandler != null) {
                                this.databaseDownHandler.handle(null);
                            }
                        }

                    });
                    JsonObject config = new JsonObject();
                    final String token = System.getProperty("KUBERNETES_SERVICE_TOKEN", "");
                    final String host = System.getProperty("KUBERNETES_SERVICE_HOST", "");
                    final boolean ssl = Boolean.valueOf(System.getProperty("KUBERNETES_SERVICE_SSL", "true"));
                    final int port = Integer.valueOf(System.getProperty("KUBERNETES_SERVICE_PORT", "0"));
                    if (!StringUtils.isEmpty(token)) {
                        config.put("token", token);
                    }
                    if (!StringUtils.isEmpty(host)) {
                        config.put("host", host);
                    }
                    if (port > 0) {
                        config.put("port", port);
                    }
                    config.put("ssl", ssl);
                    this.discoveryService.registerServiceImporter(new KubernetesServiceImporter(), config, doImport -> {
                        if (doImport.succeeded()) {
                            handler.handle(Future.succeededFuture(this.discoveryService));
                        } else {
                            handler.handle(Future.failedFuture(doImport.cause()));
                        }
                    });
                } else {
                    handler.handle(Future.succeededFuture(discoveryService));
                }
            } else {
                handler.handle(Future.failedFuture(readVertx.cause()));
            }
        });
    }

    private KubernetesDatabase createClient(KubernetesDBType type, KubernetesDatabaseConfig dbConfig, KubernetesRecordLocation loc) {
        switch (type) {
            case POSTGRESQL: {
                KubernetesSecret secret = dbConfig.secretConfig();
                String password = secret.value("password");
                String username = secret.value("username");
                String database = secret.value("database_name");
                PgConnectOptions connectOptions = new PgConnectOptions()
                        .setPort(loc.port())
                        .setHost(loc.host())
                        .setDatabase(database)
                        .setUser(username)
                        .setPassword(password);
                PoolOptions poolOptions = new PoolOptions()
                        .setMaxSize(5);
                try {
                    PgPool poolData = PgPool.pool(vertx, connectOptions, poolOptions);
                    KubernetesDatabase client = KubernetesDatabase.postgreSql(poolData, KubernetesLiveDatabase.create(vertx, PgSubscriber.subscriber(vertx, connectOptions)));
                    return client;
                } catch (Exception ex) {
                    return null;
                }
            }
            case REDIS: {
                JsonObject config = dbConfig.config();
                // keep_alive: true
                boolean keepAlive = config.getBoolean("keep_alive", true);
                //no_delay: true 
                boolean noDelay = config.getBoolean("no_delay", true);
                //mastername: mymaster
                String masterName = config.getString("mastername", "mymaster");
                //role: maste
                RedisRole role = RedisRole.valueOf(config.getString("role", "masterr").toUpperCase(Locale.ENGLISH));
                //  slaves: never       

                RedisSlaves slaves = RedisSlaves.valueOf(config.getString("slaves", "never").toUpperCase(Locale.ENGLISH));
                Redis client = Redis.createClient(vertx, new RedisOptions()
                        .setNetClientOptions(new NetClientOptions()
                                .setTcpKeepAlive(keepAlive)
                                .setTcpNoDelay(noDelay))
                        .setRole(role)
                        .setMasterName(masterName)
                        .setUseSlave(slaves)
                        .addConnectionString(StringUtils.append(loc.host(), ":", loc.port()))
                );
                return KubernetesDatabase.redis(client);

            }
            default: {
                return null;
            }
        }

    }

    @Override
    public KubernetesManager onDatabaseDown(Handler<Void> onDatabase) {
        this.databaseDownHandler = onDatabase;
        return this;
    }

}
