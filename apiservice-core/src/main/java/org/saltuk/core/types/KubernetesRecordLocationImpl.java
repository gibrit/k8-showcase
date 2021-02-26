/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public class KubernetesRecordLocationImpl implements KubernetesRecordLocation {

    private final Integer port;
    private final String host;
    private final Boolean ssl;
    private final String endpoint;

    public KubernetesRecordLocationImpl(JsonObject data) {
        this.host = data.getString("host");
        this.port = data.getInteger("port");
        this.ssl = data.getBoolean("ssl", false);
        this.endpoint = data.getString("endpoint", "");
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public boolean ssl() {
        return this.ssl;
    }

    @Override
    public String endpoint() {
        return this.endpoint;
    }

}
