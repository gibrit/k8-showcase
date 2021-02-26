/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.saltuk.core.StringUtils;
import org.saltuk.core.api.ApiPagination;

/**
 *
 * @author saltuk
 */
public class DBFilterGroupImpl implements DBFilterGroup {

    private final Set<DBFilter> filters;
    private final ApiPagination pagination;
    private final String token;

    public DBFilterGroupImpl(Set<DBFilter> filters, ApiPagination pagination) {
        this.filters = filters;
        this.pagination = pagination;
        String filterTokens = StringUtils.join("~", this.filters.stream().map(v -> v.token()).sorted().collect(Collectors.toList()));
        StringBuilder output = new StringBuilder();
        output.append(filterTokens).append("~").append(this.pagination.token());
        this.token = DigestUtils.md5Hex(output.toString());
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public Set<DBFilter> filters() {
        return this.filters;
    }

    @Override
    public ApiPagination pagination() {
        return this.pagination;
    }

}
