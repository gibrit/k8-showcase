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
public interface KubernetesRecordLocation {

    static KubernetesRecordLocation create(JsonObject data) {
        return new KubernetesRecordLocationImpl(data);
    }

    String host();

    int port();

    boolean ssl();

    String endpoint();

}
