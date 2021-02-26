/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import java.util.ArrayList;
import java.util.List;
import org.saltuk.core.StringUtils;

/**
 *
 * @author saltuk
 * @param <T>
 */
public class DBFilterImpl<T> implements DBFilter<T> {

    private final T value;
    private final String name;
    private final DBFilterType filter;
    private final boolean isArray;
    private List<T> values;
    private String token;

    public DBFilterImpl(String name, T value, DBFilterType type) {
        this.value = value;
        this.filter = type;
        this.name = name;
        this.isArray = false;
        this.values = new ArrayList();
    }

    public DBFilterImpl(String name, List<T> value, DBFilterType type) {
        this.value = null;
        this.filter = type;
        this.values = value != null ? value : new ArrayList();
        this.name = name;
        this.isArray = true;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public DBFilterType filter() {
        return this.filter;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public List<T> values() {
        return this.values;
    }

    @Override
    public boolean isArray() {
        return this.isArray;
    }

    @Override
    public String token() {
        if (StringUtils.isEmpty(this.token)) {
            StringBuilder output = new StringBuilder();
            output
                    .append(this.name).append("~")
                    .append(filter.name()).append("~")
                    .append(this.isArray).append("~")
                    .append(this.isArray ? StringUtils.join("~", this.values) : this.value);
            this.token = output.toString();
        }
        return this.token;
    }

}
