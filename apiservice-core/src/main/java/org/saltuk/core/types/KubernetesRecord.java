/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

/**
 * Kubernetes Record Data
 *
 * @author Saltık Buğra Avcı ben@saltuk.org
 */
public interface KubernetesRecord {

    static KubernetesRecord create(Record record, KubernetesModuleConfig config) {
        return new KubernetesRecordImpl(record, config);
    }

    String accessName();

    /**
     * Returns Record Name
     *
     * @return
     */
    String name();

    /**
     * Returns Record Status
     *
     * @return
     */
    Status status();

    /**
     * Returns Record Type
     *
     * @return
     */
    String type();

    /**
     * Returns Record Meta Data
     *
     * @return
     */
    KubernetesRecordMetaData metadata();

    KubernetesRecordLocation location();

    KubernetesModuleConfig config();

    KubernetesRecord update(Record record);

}
