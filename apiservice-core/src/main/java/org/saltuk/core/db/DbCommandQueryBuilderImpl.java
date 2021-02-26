/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.saltuk.core.StringUtils;
import org.saltuk.core.types.KubernetesDBType;

/**
 *
 * @author saltuk
 */
class DbCommandQueryBuilderImpl implements DbCommandQueryBuilder {

    private Tuple tuple;
    private JsonArray errors;
    private final DbCommandQueryType type;
    private DbTable table;
    private long id;
    private final KubernetesDBType dbType;

    public DbCommandQueryBuilderImpl(KubernetesDBType db, DbCommandQueryType type, DbTable table) {
        this(db, type, table, -1L);
    }

    public DbCommandQueryBuilderImpl(KubernetesDBType db, DbCommandQueryType type, DbTable table, long id) {
        this.type = type;
        this.table = table;
        this.tuple = Tuple.tuple();
        this.errors = new JsonArray();
        this.id = id;
        this.dbType = db;
    }

    @Override
    public DbCommand prepareQuery() {
        return this.prepareQuery(MultiMap.caseInsensitiveMultiMap());
    }

    @Override
    public DbCommand prepareQuery(MultiMap map) {
        switch (dbType) {
            case MYSQL:
            case POSTGRESQL: {

                switch (this.type) {

                    case UPDATE: {
                        if (id > 0) {
                            if (map.size() > 0) {

                                final StringBuilder sqlBuilder = new StringBuilder();
                                sqlBuilder.append("update ").append(this.table.name()).append(" set ");
                                final AtomicInteger fieldIndex = new AtomicInteger();
                                final Map<String, DbField> vals = map.names().stream().filter(f -> this.table.fieldExists(f)).collect(Collectors.toMap(t -> t, t -> this.table.fieldByName(t)));
                                Iterator<Map.Entry<String, DbField>> iterator = vals.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    int fieldIndexValue = fieldIndex.incrementAndGet();
                                    Map.Entry<String, DbField> next = iterator.next();

                                    DbField f = next.getValue();
                                    String val = map.get(next.getKey());
                                    if (f.validate(val)) {
                                        tuple.addValue(f.value(val));

                                        if (fieldIndexValue > 1) {
                                            sqlBuilder.append(",");
                                        }
                                        sqlBuilder.append(f.name()).append(" = ").append(this.parapaneName(fieldIndexValue));
                                    } else {
                                        errors.add(StringUtils.append(f.name(), " is invalid"));

                                    }
                                }
                                sqlBuilder.append(" where ").append(this.table.primaryField().name()).append("= ").append(this.parapaneName(fieldIndex.incrementAndGet()));
                                tuple.addValue(id);
                                if (dbType == KubernetesDBType.POSTGRESQL) {
                                    sqlBuilder.append(" returning  *");
                                }
                                return errors.isEmpty() ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                            } else {
                                return DbCommand.fail(new JsonArray().add("No Data"));
                            }
                        } else {
                            return DbCommand.fail(new JsonArray().add("Id is not found"));
                        }

                    }
                    case CREATE: {
                        if (map.size() > 0) {

                            final StringBuilder sqlBuilder = new StringBuilder();
                            final StringBuilder valueBuilder = new StringBuilder();
                            final AtomicInteger index = new AtomicInteger();
                            sqlBuilder.append("insert into  ").append(this.table.name()).append("( ");
                            boolean isValid = map.names().stream().allMatch(f -> {
                                if (table.fieldExists(f)) {
                                    DbField field = table.fieldByName(f);
                                    if (field.validate(map.get(f))) {
                                        int fieldIndex = index.incrementAndGet();
                                        tuple.addValue(field.value(f));
                                        if (fieldIndex > 1) {
                                            valueBuilder.append(",");
                                            sqlBuilder.append(",");
                                        }
                                        valueBuilder.append(this.parapaneName(fieldIndex));
                                        sqlBuilder.append(field.name());
                                        return true;
                                    }
                                    return false;
                                }
                                this.errors.add(StringUtils.append(f, " is not a valid field "));
                                return false;
                            });
                            sqlBuilder.append(") values (").append(valueBuilder).append(")");
                            if (dbType == KubernetesDBType.POSTGRESQL) {
                                sqlBuilder.append(" returning  *");
                            }
                            return isValid ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data"));
                        }

                    }

                    default: {
                        if (this.id > 0) {
                            StringBuilder output = new StringBuilder();
                            output.append("delete from ").append(this.table.name()).append(" where ").append(table.primaryField().name()).append("= ").append(this.parapaneName(1));
                            if (dbType == KubernetesDBType.POSTGRESQL) {
                                output.append(" returning  *");
                            }
                            tuple.addLong(this.id);
                            return DbCommand.sql(output.toString(), tuple);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data Id "));
                        }
                    }
                }
            }
            default: {
                return DbCommand.fail(new JsonArray(StringUtils.append(this.dbType.name(), " is not supported for now")));
            }
        }

    }

    @Override
    public DbCommand prepareQuery(JsonObject map) {

        switch (dbType) {
            case MYSQL:
            case POSTGRESQL: {

                switch (this.type) {

                    case UPDATE: {
                        if (id > 0) {
                            if (map.size() > 0) {

                                final StringBuilder sqlBuilder = new StringBuilder();
                                sqlBuilder.append("update ").append(this.table.name()).append(" set ");
                                final AtomicInteger fieldIndex = new AtomicInteger();
                                final Map<String, DbField> vals = map.fieldNames().stream().filter(f -> this.table.fieldExists(f)).collect(Collectors.toMap(t -> t, t -> this.table.fieldByName(t)));
                                Iterator<Map.Entry<String, DbField>> iterator = vals.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    int fieldIndexValue = fieldIndex.incrementAndGet();
                                    Map.Entry<String, DbField> next = iterator.next();

                                    DbField f = next.getValue();

                                    if (f.validateValue(map.getValue(next.getKey()))) {
                                        tuple.addValue(map.getValue(next.getKey()));

                                        if (fieldIndexValue > 1) {
                                            sqlBuilder.append(",");
                                        }
                                        sqlBuilder.append(f.name()).append(" = ").append(this.parapaneName(fieldIndexValue));
                                    } else {
                                        errors.add(StringUtils.append(f.name(), " is invalid"));

                                    }
                                }
                                sqlBuilder.append(" where ").append(this.table.primaryField().name()).append("= ").append(this.parapaneName(fieldIndex.incrementAndGet()));
                                tuple.addValue(id);
                                if (dbType == KubernetesDBType.POSTGRESQL) {
                                    sqlBuilder.append(" returning  *");
                                }
                                return errors.isEmpty() ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                            } else {
                                return DbCommand.fail(new JsonArray().add("No Data"));
                            }
                        } else {
                            return DbCommand.fail(new JsonArray().add("Id is not found"));
                        }

                    }
                    case CREATE: {
                        if (map.size() > 0) {

                            final StringBuilder sqlBuilder = new StringBuilder();
                            final StringBuilder valueBuilder = new StringBuilder();
                            final AtomicInteger index = new AtomicInteger();
                            sqlBuilder.append("insert into  ").append(this.table.name()).append("( ");
                            boolean isValid = map.fieldNames().stream().allMatch(f -> {
                                if (table.fieldExists(f)) {
                                    DbField field = table.fieldByName(f);
                                    if (field.validateValue(map.getValue(f))) {
                                        int fieldIndex = index.incrementAndGet();
                                        tuple.addValue(map.getValue(f));
                                        if (fieldIndex > 1) {
                                            valueBuilder.append(",");
                                            sqlBuilder.append(",");
                                        }
                                        valueBuilder.append(this.parapaneName(fieldIndex));
                                        sqlBuilder.append(field.name());
                                        return true;
                                    }
                                    return false;
                                }
                                this.errors.add(StringUtils.append(f, " is not a valid field "));
                                return false;
                            });
                            sqlBuilder.append(") values (").append(valueBuilder).append(")");
                            if (dbType == KubernetesDBType.POSTGRESQL) {
                                sqlBuilder.append(" returning  *");
                            }
                            return isValid ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data"));
                        }

                    }

                    default: {
                        if (map.getLong(table.primaryField().name(), -1L) > 0) {
                            StringBuilder output = new StringBuilder();
                            output.append("delete from ").append(this.table.name()).append(" where ").append(table.primaryField().name()).append("= ").append(this.parapaneName(1));
                            if (dbType == KubernetesDBType.POSTGRESQL) {
                                output.append(" returning  *");
                            }
                            return DbCommand.sql(output.toString(), tuple);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data Id "));
                        }
                    }
                }
            }
            default: {
                return DbCommand.fail(new JsonArray(StringUtils.append(this.dbType.name(), " is not supported for now")));
            }
        }

    }

    @Override
    public DbCommand prepareQuery(JsonArray map) {
        switch (dbType) {
            case MYSQL:
            case POSTGRESQL: {

                switch (this.type) {

                    /*   case UPDATE: {
                        if (id > 0) {
                            if (map.size() > 0) {

                                final StringBuilder sqlBuilder = new StringBuilder();
                                sqlBuilder.append("update ").append(this.table.name()).append(" set ");
                                final AtomicInteger fieldIndex = new AtomicInteger();
                                final Map<String, DbField> vals = map.fieldNames().stream().filter(f -> this.table.fieldExists(f)).collect(Collectors.toMap(t -> t, t -> this.table.fieldByName(t)));
                                Iterator<Map.Entry<String, DbField>> iterator = vals.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    int fieldIndexValue = fieldIndex.incrementAndGet();
                                    Map.Entry<String, DbField> next = iterator.next();

                                    DbField f = next.getValue();
                                    
                                    if (f.validateValue(map.getValue(next.getKey()))) {
                                        tuple.addValue(map.getValue(next.getKey()));

                                        if (fieldIndexValue > 1) {
                                            sqlBuilder.append(",");
                                        }
                                        sqlBuilder.append(f.name()).append(" = ").append(this.parapaneName(fieldIndexValue));
                                    } else {
                                        errors.add(StringUtils.append(f.name(), " is invalid"));

                                    }
                                }
                                sqlBuilder.append(" where ").append(this.table.primaryField().name()).append("= ").append(this.parapaneName(fieldIndex.incrementAndGet()));
                                tuple.addValue(id);
                                return errors.isEmpty() ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                            } else {
                                return DbCommand.fail(new JsonArray().add("No Data"));
                            }
                        } else {
                            return DbCommand.fail(new JsonArray().add("Id is not found"));
                        }

                    }*/
                    case CREATE: {
                        if (map.size() > 0) {

                            final StringBuilder sqlBuilder = new StringBuilder();
                            final StringBuilder valueBuilder = new StringBuilder();
                            final AtomicInteger index = new AtomicInteger();
                            final AtomicInteger rowIndex = new AtomicInteger();
                            sqlBuilder.append("insert into  ").append(this.table.name()).append("( ");
                            boolean isValid = map.getList().stream().allMatch(row -> {
                                final JsonObject data = (JsonObject) row;
                                int rowCurrentIndex = rowIndex.getAndIncrement();
                                if (rowCurrentIndex > 0) {
                                    valueBuilder.append(",");
                                }
                                valueBuilder.append("(");
                                boolean res = data.fieldNames().stream().allMatch(f -> {
                                    if (table.fieldExists(f)) {
                                        DbField field = table.fieldByName(f);
                                        if (field.validateValue(data.getValue(f))) {
                                            int fieldIndex = index.incrementAndGet();
                                            tuple.addValue(data.getValue(f));
                                            if (fieldIndex > 1) {
                                                valueBuilder.append(",");
                                                if (rowCurrentIndex == 0) {
                                                    sqlBuilder.append(",");
                                                }
                                            }
                                            valueBuilder.append(this.parapaneName(fieldIndex));
                                            if (rowCurrentIndex == 0) {
                                                sqlBuilder.append(field.name());
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                    this.errors.add(StringUtils.append(f, " is not a valid field  on Item", rowCurrentIndex));
                                    return false;
                                });
                                valueBuilder.append(")");
                                return res;
                            });
                            sqlBuilder.append(") values (").append(valueBuilder).append(")");
                            return isValid ? DbCommand.sql(sqlBuilder.toString(), tuple) : DbCommand.fail(errors);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data"));
                        }

                    }
                    case DELETE: {
                        final AtomicInteger currentIndex = new AtomicInteger();
                        StringBuilder output = new StringBuilder();
                        output.append("delete from ").append(this.table.name()).append(" where ").append(table.primaryField().name()).append("in ( ");

                        boolean isValid = map.getList().stream().allMatch(v -> {
                            if (v instanceof Long) {
                                tuple.addLong(id);
                                output.append(this.parapaneName(currentIndex.getAndIncrement()));
                                return true;
                            } else {
                                this.errors.add(StringUtils.append(v, " is not a valid ID value  on Item :", currentIndex.getAndIncrement()));
                                return false;
                            }
                        });
                        output.append(")");
                        return isValid ? DbCommand.sql(output.toString(), tuple) : DbCommand.fail(errors);
                    }

                    default: {
                        /*   if (this.id > 0) {
                            StringBuilder output = new StringBuilder();
                            output.append("delete from ").append(this.table.name()).append(" where ").append(table.primaryField().name()).append("= ").append(this.parapaneName(1));
                            return DbCommand.sql(output.toString(), tuple);
                        } else {
                            return DbCommand.fail(new JsonArray().add("No Data Id "));
                        }
                         */
                        return DbCommand.fail(new JsonArray().add("Supported Only Create and Delete Actions Actions"));
                    }
                }
            }
            default: {
                return DbCommand.fail(new JsonArray(StringUtils.append(this.dbType.name(), " is not supported for now")));
            }
        }
    }

    private String parapaneName(int fieldIndexValue) {
        return this.dbType == KubernetesDBType.POSTGRESQL ? StringUtils.append("$", fieldIndexValue) : "?";
    }

}
