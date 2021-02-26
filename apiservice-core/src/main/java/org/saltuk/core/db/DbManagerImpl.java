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

import java.util.stream.IntStream;
import org.saltuk.core.db.annotations.Field;
import org.saltuk.core.db.annotations.Id;
import org.saltuk.core.db.annotations.Table;

/**
 *
 * @author saltuk
 */
class DbManagerImpl implements DbManager {

    private static DbManager instance;

    public static synchronized DbManager newInstance() {
        if (instance == null) {
            instance = new DbManagerImpl();
        }
        return instance;
    }

    private DbManagerImpl() {
    }

    @Override
    public DbTable build(Class clazz) {

        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            String tableName = table.value();
            final DbTable dTable = DbTable.create(tableName);
            final java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            IntStream.range(0, fields.length).forEach(i -> {
                DbField field = this.parseField(fields[i]);
                if (field != null) {
                    dTable.field(field);
                }
            });
            return dTable;
        }
        return null;
    }

    private DbField parseField(java.lang.reflect.Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            DbFieldType fieldType = this.getFieldType(field.getType());

            return DbField.create().name("id").notNull(true).primaryKey(true).type(fieldType);
        }
        if (field.isAnnotationPresent(Field.class)) {
            DbFieldType fieldType = this.getFieldType(field.getType());
            Field aField = field.getAnnotation(Field.class);
            return DbField.create().name(aField.name()).notNull(aField.notNull()).primaryKey(false).type(fieldType).hidden(aField.hidden()).size(aField.size());
        }

        return null;
    }

    private DbFieldType getFieldType(Class<?> type) {
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return DbFieldType.INT;
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return DbFieldType.LONG;
        }
        if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return DbFieldType.FLOAT;
        }

        return DbFieldType.TEXT;
    }

}
