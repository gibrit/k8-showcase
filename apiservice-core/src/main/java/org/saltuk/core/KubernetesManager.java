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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Set;
import org.saltuk.core.types.KubernetesDatabase;
import org.saltuk.core.types.KubernetesModuleConfig;
import org.saltuk.core.types.KubernetesRecord;
import org.saltuk.core.types.KubernetesSecret;

/**
 * K8s Manager
 *
 * @author Saltık Buğra Avcı ben@saltuk.org
 */
public interface KubernetesManager {

    static KubernetesManager newInstance() {
        return KubernetesManagerImpl.newInstance();
    }

    static KubernetesManager kubernetesDebugInstance(String token, String host, int port, boolean isSSL) {
        System.setProperty("KUBERNETES_SERVICE_PORT", String.valueOf(port));
        System.setProperty("KUBERNETES_SERVICE_HOST", host);
        System.setProperty("KUBERNETES_SERVICE_TOKEN", token);
        System.setProperty("KUBERNETES_SERVICE_SSL", String.valueOf(isSSL));
        System.setProperty("KUBERNETES_SERVICE_DEBUG", "true");
        return newInstance();

    }

    /**
     * creates or get Instance of Clustered Vert.x Object
     *
     * @param handler
     */
    void vertx(Handler<AsyncResult<Vertx>> handler);

    /**
     * Return K8 Record
     *
     * @param name
     * @param handler
     */
    void service(String name, Handler<AsyncResult<KubernetesRecord>> handler);

    /**
     * Return K8 Records
     *
     * @param nameservice
     * @param name
     * @param handler
     */
    void service(String nameservice, Set<String> name, Handler<AsyncResult<Set<KubernetesRecord>>> handler);

    /**
     * Return K8 Records
     *
     * @param name
     * @param handler
     */
    void service(Set<String> name, Handler<AsyncResult<Set<KubernetesRecord>>> handler);

    /**
     * Return K8 Record
     *
     * @param nameservice
     * @param name
     * @param handler
     */
    void service(String nameservice, String name, Handler<AsyncResult<KubernetesRecord>> handler);

    /**
     * Service Database Connection from Service Connection
     *
     * @param serviceName
     * @param handler
     */
    void database(String serviceName, Handler<AsyncResult<KubernetesDatabase>> handler);

    /**
     * Read ConfigMap
     *
     * @param namespace
     * @param serviceName
     * @param handler
     */
    void config(String namespace, String serviceName, Handler<AsyncResult<JsonObject>> handler);

    /**
     * Read ConfigMap
     *
     * @param serviceName
     * @param handler
     */
    void config(String serviceName, Handler<AsyncResult<JsonObject>> handler);

    /**
     * Read ConfigMaps
     *
     * @param serviceNames
     * @param handler
     */
    void configs(Set<String> serviceNames, Handler<AsyncResult<JsonArray>> handler);

    /**
     * Read ConfigMaps
     *
     * @param namespace
     * @param serviceNames
     * @param handler
     */
    void configs(String namespace, Set<String> serviceNames, Handler<AsyncResult<JsonArray>> handler);

    /**
     * Read Secret
     *
     * @param serviceName
     * @param handler
     */
    void secret(String serviceName, Handler<AsyncResult<KubernetesSecret>> handler);

    /**
     * Read Secret
     *
     * @param namespace
     * @param serviceName
     * @param handler
     */
    void secret(String namespace, String serviceName, Handler<AsyncResult<KubernetesSecret>> handler);

    /**
     * Read Module Configuration
     *
     * @param name
     * @param handler
     */
    void moduleConfig(String name, Handler<AsyncResult<KubernetesModuleConfig>> handler);

    /**
     * Read Module Configuration
     *
     * @param namespace
     * @param name
     * @param handler
     */
    void moduleConfig(String namespace, String name, Handler<AsyncResult<KubernetesModuleConfig>> handler);
    
    /**
     * On Database Down  Event 
     * @param onDatabase
     * @return 
     */
    KubernetesManager onDatabaseDown(Handler<Void> onDatabase);

}
