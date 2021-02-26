/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.saltuk.core.db.DbField;

/**
 *
 * @author saltuk
 */
public class DBFieldFilterImpl implements DBFieldFilter {

    private final DbField field;
    private final Map<String, DBFilterType> filterNames;

    public DBFieldFilterImpl(DbField field) {
        this.field = field;
        this.filterNames = DBFilterType.asMap(field);
    }

    @Override
    public Set<DBFilter> prepare(MultiMap map) {
        final Set<DBFilter> filters = new HashSet<>();
        final Set<String> names = map.names().stream().filter(v -> field.filterNames().containsKey(v)).collect(Collectors.toSet());

        names.forEach(v -> {
            filters.add(DBFilter.create(field, map.get(v), field.filterNames().get(v)));
        });
        return filters;
    }

    @Override
    public DBFilter prepare(Map.Entry<String, String> value) {
        DBFilterType filterType = this.filterNames.get(value.getKey());
        if (filterType != null) {
            return DBFilter.create(field, value.getValue(), filterType);
        }
        return null;

    }

    @Override
    public Set<DBFilter> prepare(JsonObject data) {
        final Set<DBFilter> filters = new HashSet<>();
        final Set<String> names = data.fieldNames().stream().filter(v -> field.filterNames().containsKey(v)).collect(Collectors.toSet());

        names.forEach(v -> {
            if (data.getValue(v, null) != null) {
                filters.add(DBFilter.create(field, String.valueOf(data.getValue(v)), field.filterNames().get(v)));
            }
        });
        return filters;
    }

}
