/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.saltuk.core.StringUtils;
import org.saltuk.core.db.DbField;

/**
 * Database Filter Clause
 *
 * @author saltuk
 * @param <T> Value Type
 */
public interface DBFilter<T> {

    /**
     * Create Filter
     *
     * @param field
     * @param value
     * @param filter
     * @return
     */
    static DBFilter create(DbField field, String value, DBFilterType filter) {
        switch (field.type()) {
            case FLOAT: {
                return floatFilter(field.name(), value, filter);
            }
            case INT: {
                return intFilter(field.name(), value, filter);
            }
            case LONG: {
                return longFilter(field.name(), value, filter);
            }
            default: {
                return stringFilter(field.name(), value, filter);
            }
        }
    }

    /* Integer Filter
     *
     * @param name
     * @param value
     * @param filter
     * @return
     */
    static DBFilter<Integer> intFilter(String name, String value, DBFilterType filter) {
        if (StringUtils.isEmpty(value) || !filter.numeric()) {
            return null;
        }
        switch (filter) {
            case EQUALS:
            case NOT:
            case MIN:
            case MAX: {
                return new DBFilterImpl<>(name, Integer.valueOf(value), filter);
            }
            default: {
                String[] split = value.split(",");
                IntStream range = IntStream.range(0, split.length);
                final List<Integer> vals = new ArrayList();
                range.forEach(v -> {
                    String val = split[v];
                    if (!StringUtils.isEmpty(val)) {
                        vals.add(Integer.valueOf(val));
                    }
                });
                return new DBFilterImpl<>(name, vals, filter);
            }
        }

    }

    /**
     * Float Filter
     *
     * @param name
     * @param value
     * @param filter
     * @return
     */
    static DBFilter<Float> floatFilter(String name, String value, DBFilterType filter) {
        if (StringUtils.isEmpty(value) || !filter.numeric()) {
            return null;
        }
        switch (filter) {
            case EQUALS:
            case NOT:
            case MIN:
            case MAX: {
                return new DBFilterImpl<>(name, Float.valueOf(value), filter);
            }
            default: {
                String[] split = value.split(",");
                IntStream range = IntStream.range(0, split.length);
                final List<Float> vals = new ArrayList();
                range.forEach(v -> {
                    String val = split[v];
                    if (!StringUtils.isEmpty(val)) {
                        vals.add(Float.valueOf(val));
                    }
                });
                return new DBFilterImpl<>(name, vals, filter);
            }
        }

    }

    /**
     * Long FIlter
     *
     * @param name
     * @param value
     * @param filter
     * @return
     */
    static DBFilter<Long> longFilter(String name, String value, DBFilterType filter) {
        if (StringUtils.isEmpty(value) || !filter.numeric()) {
            return null;
        }
        switch (filter) {
            case EQUALS:
            case NOT:
            case MIN:
            case MAX: {
                return new DBFilterImpl<>(name, Long.valueOf(value), filter);
            }
            default: {
                String[] split = value.split(",");
                IntStream range = IntStream.range(0, split.length);
                final List<Long> vals = new ArrayList();
                range.forEach(v -> {
                    String val = split[v];
                    if (!StringUtils.isEmpty(val)) {
                        vals.add(Long.valueOf(val));
                    }
                });
                return new DBFilterImpl<>(name, vals, filter);
            }
        }
    }

    /**
     * String Filter
     *
     * @param name
     * @param value
     * @param filter
     * @return
     */
    static DBFilter<String> stringFilter(String name, String value, DBFilterType filter) {
        switch (filter) {
            case EQUALS:

            case NOT: {
                return new DBFilterImpl<>(name, value, filter);
            }
            case LIKE:
            case NOT_LIKE: {
                return new DBFilterImpl<>(name, "%" + value + "%", filter);
            }
            case MAX:
            case MIN: {
                return null;
            }
            default: {
                List<String> vals = Arrays.asList(value.split(","));
                return new DBFilterImpl<>(name, vals, filter);
            }
        }
    }

    String name();

    boolean isArray();

    DBFilterType filter();

    T value();

    List<T> values();

    String token();

}
