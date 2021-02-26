/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import java.util.Set;
import org.saltuk.core.api.ApiPagination;

/**
 *
 * @author saltuk
 */
public interface DBFilterGroup {

    static DBFilterGroup create(Set<DBFilter> filters, ApiPagination pagination) {
        return new DBFilterGroupImpl(filters, pagination);
    }

    String token();

    Set<DBFilter> filters();

    ApiPagination pagination();
}
