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

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.saltuk.core.StringUtils;
import org.saltuk.core.db.query.filter.DBFilterType;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * @author saltuk
 */
class DbFieldImpl implements DbField {

    private String name;
    private DbFieldType type;
    private boolean primary;
    private int size;
    private boolean notNull;
    private boolean hidden;
    private Map<String, DBFilterType> filters;

    public DbFieldImpl() {

    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean notNull() {
        return this.notNull;
    }

    @Override
    public DbField notNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }

    @Override
    public boolean primaryKey() {
        return this.primary;
    }

    @Override
    public DbFieldType type() {
        return this.type;
    }

    @Override
    public DbField name(String name) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        return this;
    }

    @Override
    public DbField primaryKey(boolean primary) {
        this.primary = primary;
        return this;
    }

    @Override
    public DbField type(DbFieldType type) {
        this.type = type;
        return this;
    }

    @Override
    public DbField size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean hidden() {
        return this.hidden;
    }

    @Override
    public DbField hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String query(KubernetesDBType dbType) {
        StringBuilder output = new StringBuilder();

        output
                .append(this.name)
                .append(" ");
        if (this.primary) {
            if (dbType == KubernetesDBType.POSTGRESQL) {
                output.append(" serial ");
            } else {
                output.append(this.type.name()).append(" ").append(" auto increment  ");
            }
        } else {
            if (type != DbFieldType.TEXT) {
                output.append(this.type.name());
            } else {
                if (this.size > 0) {
                    output.append("varchar(").append(this.size).append(") ");
                } else {
                    output.append(this.type.name());
                }
                
            }
        }
        output.append(this.notNull ? " NOT NULL ,\n " : " ,\n");
        return output.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbFieldImpl other = (DbFieldImpl) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public boolean validate(String value) {
        return (this.value(value) != null && this.notNull) || (!this.notNull);
    }

    @Override
    public boolean validateValue(Object value) {
        return (value != null && this.notNull) || (!this.notNull);
    }

    @Override
    public Object value(String value) {

        switch (this.type) {
            case FLOAT: {
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return Float.valueOf(value);
            }
            case INT: {
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return Integer.valueOf(value);
            }
            case LONG: {
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return Long.valueOf(value);
            }
            default: {
                return value;
            }

        }
    }

    @Override
    public Map<String, DBFilterType> filterNames() {
        if (filters == null) {
            this.filters = DBFilterType.asMap(this);
        }
        return this.filters;
    }

}
