/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author saltuk
 */
public class KubernetesDatabaseConfigImpl implements KubernetesDatabaseConfig {

    private boolean secret;
    private KubernetesSecret secretData;
    private JsonObject config;
    private int port;
    private String host;
    private KubernetesDBType type;

    public KubernetesDatabaseConfigImpl() {
    }

    public KubernetesDatabaseConfigImpl(KubernetesSecret secret) {
        this.secret = true;
        this.secretData = secret;
        this.config = new JsonObject();
    }

    @Override
    public KubernetesDatabaseConfig secretConfig(KubernetesSecret secret) {
        this.secret = true;
        this.secretData = secret;
        return this;
    }

    @Override
    public KubernetesDatabaseConfig config(JsonObject config) {
        this.config = config;
        this.secret = false;
        return this;
    }

    @Override
    public KubernetesSecret secretConfig() {
        return this.secretData;
    }

    @Override
    public boolean secret() {
        return this.secret;
    }

    @Override
    public JsonObject config() {
        return this.config;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public KubernetesDatabaseConfig port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public KubernetesDatabaseConfig host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public KubernetesDBType type() {
        return this.type;
    }

    @Override
    public KubernetesDatabaseConfig type(KubernetesDBType type) {
        this.type = type;
        return this;
    }

}
