/*
 * Copyright 2020 saltuk.
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
package org.saltuk.core.db;

import java.util.Collection;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * @author saltuk
 */
public interface DbTable {

    static DbTable create(String name) {
        return new DbTableImpl(name);
    }

    String name();

    Collection<DbField> fields();

    DbTable field(DbField field);

    DbField primaryField();

    DbField fieldByName(String name);

    /**
     * Table Create Query
     *
     * @param dbType
     * @return
     */
    String query(KubernetesDBType dbType);

    /**
     * Table Live Trigger
     *
     * @return
     */
    String triggerQuery();

    /**
     * SQL Select Query
     *
     * @return
     */
    StringBuilder selectQuery();

    /**
     * Sql Count Query
     *
     * @return
     */
    StringBuilder countQuery();

    /**
     * Check Field name exists or not
     *
     * @param name
     * @return
     */
    boolean fieldExists(String name);

}
