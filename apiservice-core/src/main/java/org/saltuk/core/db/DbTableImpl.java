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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * @author saltuk
 */
public class DbTableImpl implements DbTable {

    private final String name;

    private final Map<String, DbField> fields;
    private DbField primaryField;
    private StringBuilder sqlBuilder;
    private StringBuilder countBuilder;

    public DbTableImpl(String name) {
        this.name = name;
        this.fields = new ConcurrentHashMap<>();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Collection<DbField> fields() {

        return this.fields.values();
    }

    @Override
    public DbTable field(DbField field) {
        if (field != null) {
            this.fields.put(field.name(), field);
            if (field.primaryKey()) {
                this.primaryField = field;
            }
        }
        return this;
    }

    @Override
    public DbField primaryField() {
        return this.primaryField;
    }

    @Override
    public String query(KubernetesDBType type) {
        switch (type) {
            case POSTGRESQL: {
                StringBuilder output = new StringBuilder();
                output.append("CREATE TABLE  IF NOT EXISTS  ")
                        .append(name)
                        .append(" (\n");
                this.fields.values().forEach(v -> {
                    output.append(v.query(type));
                });
                output.append(" CONSTRAINT pk_").append(this.name).append(" PRIMARY KEY (").append(this.primaryField.name()).append(")\n");
                output.append("\n);");
                output.append("DROP  TRIGGER IF EXISTS  db_").append(this.name).append("_db_notify_live ON public.users;");
                   output.append("CREATE TRIGGER db_").append(this.name).append("_db_notify_live AFTER INSERT OR UPDATE OR DELETE ON ").append(this.name).append(" FOR EACH STATEMENT EXECUTE PROCEDURE  db_notify_live();");
                return output.toString();
            }
            case MYSQL: {
                StringBuilder output = new StringBuilder();
                output.append("CREATE TABLE  IF NOT EXISTS  ")
                        .append(name)
                        .append(" {"
                                + "(\n");
                this.fields.values().forEach(v -> {
                    output.append(v.query(type));
                });
                output.append(" CONSTRAINT pk_").append(this.name).append(" PRIMARY KEY (").append(this.primaryField.name()).append(")\n");
                output.append("\n);");
                output.append("CREATE TRIGGER db_").append(this.name).append("_db_notify_live AFTER INSERT OR UPDATE OR DELETE ON  public.").append(this.name).append(" FOR EACH STATEMENT EXECUTE PROCEDURE  db_notify_live();");
                return output.toString();
            }
            default: {
                return "";
            }

        }
    }

    @Override
    public String triggerQuery() {
        throw new UnsupportedOperationException("Not supported yet.  PostgreSql notify and listen events trigger for Live Query "); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringBuilder selectQuery() {
        if (this.sqlBuilder == null) {
            this.sqlBuilder = new StringBuilder();
            this.sqlBuilder.append("select ");
            SqlBuilderData isFirst = new SqlBuilderData();
            this.fields.values().forEach(v -> {
                if (!v.hidden()) {
                    if (!isFirst.isFirst()) {
                        this.sqlBuilder.append(",");
                    }
                    this.sqlBuilder.append(v.name());
                }
            });
            this.sqlBuilder.append(" from ").append(this.name);
        }
        return sqlBuilder;
    }

    @Override
    public StringBuilder countQuery() {
        if (this.countBuilder == null) {
            this.countBuilder = new StringBuilder();
            this.countBuilder.append(" select count(*) as total  from ").append(name);
        }
        return this.countBuilder;

    }

    @Override
    public boolean fieldExists(String name) {
        return this.fields.get(name.toLowerCase(Locale.ENGLISH)) != null;
    }

    @Override
    public DbField fieldByName(String name) {
        return this.fields.get(name.toLowerCase(Locale.ENGLISH));
    }

    private class SqlBuilderData {

        private boolean first;

        public SqlBuilderData() {
            first = true;
        }

        boolean isFirst() {
            if (first) {
                first = false;
                return true;
            }
            return first;
        }
    }
}
