/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;
import java.util.Locale;
import org.saltuk.core.StringUtils;

/**
 *
 * @author Saltık Buğra Avcı ben@saltuk.org
 */
class KubernetesRecordMetaDataImpl implements KubernetesRecordMetaData {
    
    private final String app;
    private final String uid;
    private final String name;
    private final String namespace;
    
    public KubernetesRecordMetaDataImpl(JsonObject data) {
        this.app = data.getString("app");
        this.namespace = data.getString("kubernetes.namespace");
        this.name = data.getString("kubernetes.name");
        this.uid = data.getString("kubernetes.uuid");
    }
    
    @Override
    public String application() {
        return this.app;
    }
    
    @Override
    public String namespace() {
        return this.namespace;
    }
    
    @Override
    public String name() {
        return this.name;
    }
    
    @Override
    public String uid() {
        return this.uid;
    }
    
    @Override
    public String accessName() {
        return StringUtils.append(namespace, ".", name).toLowerCase(Locale.ENGLISH);
    }
    
}
