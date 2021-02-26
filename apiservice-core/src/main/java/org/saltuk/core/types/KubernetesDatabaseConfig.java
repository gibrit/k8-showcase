/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;

/**
 * K8 Database Configuration
 *
 * @author saltuk
 */
public interface KubernetesDatabaseConfig {

    static KubernetesDatabaseConfig create() {
        return new KubernetesDatabaseConfigImpl();
    }

    KubernetesSecret secretConfig();

    KubernetesDatabaseConfig secretConfig(KubernetesSecret secret);

    boolean secret();

    JsonObject config();

    KubernetesDatabaseConfig config(JsonObject config);

    int port();

    KubernetesDatabaseConfig port(int port);

    String host();

    KubernetesDatabaseConfig host(String host);

    KubernetesDBType type();

    KubernetesDatabaseConfig type(KubernetesDBType type);
}
