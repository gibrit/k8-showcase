/*
 * Copyright 2019 saltuk.
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
package org.saltuk.core.types;

import io.vertx.core.json.JsonObject;
import java.util.Locale;
import org.saltuk.core.json.JsonDataBase;

/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
class KubernetesModuleConfigImpl extends JsonDataBase implements KubernetesModuleConfig {

    private String version;
    private String endpoint;
    private String name;
    private String dbService;
    private String alias;
    private KubernetesModuleType type;
    private KubernetesDBType dbType;

    public KubernetesModuleConfigImpl() {

    }

    @Override
    public JsonObject asJson() {

        return new JsonObject()
                .put("alias", this.alias)
                .put("name", name)
                .put("endpoint", this.endpoint)
                .put("version", version)
                .put("type", this.type.name().toLowerCase(Locale.ENGLISH))
                .put("db_type", this.dbType.name().toLowerCase(Locale.ENGLISH))
                .put("db", dbService);
    }

    @Override
    public void fromJson(JsonObject value) {
        this.alias = value.getString("alias");
        this.name = value.getString("name");
        this.endpoint = value.getString("endpoint");
        this.version = value.getString("version");
        this.dbService = value.getString("db");
        this.dbType = KubernetesDBType.valueOf(value.getString("db_type", "POSTGRESQL").toUpperCase(Locale.ENGLISH));
        this.type = KubernetesModuleType.valueOf(value.getString("type", "WEB_API").toUpperCase(Locale.ENGLISH));

    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String endpoint() {
        return this.endpoint;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public String dbService() {
        return this.dbService;
    }

    @Override
    public KubernetesModuleType type() {
        return this.type;
    }

    @Override
    public KubernetesDBType dbType() {
        return this.dbType;
    }

}
