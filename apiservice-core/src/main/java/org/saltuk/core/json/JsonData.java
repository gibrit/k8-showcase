package org.saltuk.core.json;

/*
 * Copyright 2016 saltuk.
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


import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.core.shareddata.impl.ClusterSerializable;

/**
 * Implemented Object Becomes Shareable
 *
 * Use {@link JsonDataBase} abstract class implement
 *
 * @author  Saltık Buğra Avcı ben@saltuk.org
 */
public interface JsonData extends ClusterSerializable, Shareable  {

    /**
     * Returns Object As JsonObject
     *
     * @return
     */
    JsonObject asJson();

    /**
     * reads Object values from JsonObject
     *
     * @param value
     */
    void fromJson(JsonObject value);
}
