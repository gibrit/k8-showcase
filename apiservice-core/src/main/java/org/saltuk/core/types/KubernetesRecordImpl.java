/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

/**
 *
 * @author Saltık Buğra Avcı ben@saltuk.org
 */
public class KubernetesRecordImpl implements KubernetesRecord {

    private final String name;
    private final Status status;
    private final String type;
    private final KubernetesRecordLocation location;
    private final KubernetesRecordMetaData metadata;
    private final KubernetesModuleConfig config;

    public KubernetesRecordImpl(Record record, KubernetesModuleConfig config) {
        this.name = record.getName();
        this.status = record.getStatus();
        this.type = record.getType();
        this.location = KubernetesRecordLocation.create(record.getLocation());
        this.metadata = KubernetesRecordMetaData.create(record.getMetadata());
        this.config = config;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Status status() {
        return this.status;
    }

    @Override
    public String accessName() {
        return this.metadata.accessName();
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public KubernetesRecordMetaData metadata() {
        return this.metadata;
    }

    @Override
    public KubernetesRecordLocation location() {
        return this.location;
    }

    @Override
    public KubernetesModuleConfig config() {
        return this.config;
    }

    @Override
    public KubernetesRecord update(Record record) {
        return new KubernetesRecordImpl(record, config);
    }

}
