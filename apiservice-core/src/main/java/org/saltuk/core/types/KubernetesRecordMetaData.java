/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;

/**
 * Kubernetes Record Metadata
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public interface KubernetesRecordMetaData {

    static KubernetesRecordMetaData create(JsonObject data) {
        return new KubernetesRecordMetaDataImpl(data);
    }

    /**
     * Returns Application name
     *
     * @return
     */
    String application();

    /**
     * Returns name space
     *
     * @return
     */
    String namespace();

    /**
     * Returns name
     *
     * @return
     */
    String name();

    /**
     * Returns uid
     *
     * @return
     */
    String uid();
    
    /**
     * Returns namespace.name  for Access Name 
     * @return 
     */
    String accessName();

}
