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
import org.saltuk.core.json.JsonData;

/**
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public interface KubernetesModuleConfig extends JsonData {

    static KubernetesModuleConfig create(JsonObject data) {
        if (data != null && !data.isEmpty()) {
            KubernetesModuleConfigImpl result = new KubernetesModuleConfigImpl();
            result.fromJson(data);
            return result;
        }
        return null;
    }

    String name();

    String endpoint();

    String alias();

    String version();

    String dbService();
    
    KubernetesModuleType type(); 
    
    KubernetesDBType dbType(); 

}
