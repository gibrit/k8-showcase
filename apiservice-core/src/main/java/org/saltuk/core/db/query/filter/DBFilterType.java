/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.saltuk.core.StringUtils;
import org.saltuk.core.db.DbField;
import org.saltuk.core.db.DbFieldType;

/**
 * Filters Types
 *
 * @author saltuk- Saltik Bugra Avci ben@saltuk.org
 */
public enum DBFilterType {
    EQUALS(true, true),
    MIN(false, true),
    MAX(false, true),
    IN(true, true),
    LIKE(true, true),
    NOT(true, true),
    NOT_IN(true, true),
    NOT_LIKE(true, false);
    private final boolean isText;
    private final boolean isNumeric;

    public static DBFilterType fromName(String fieldName, String paramName) {
        String val = paramName.startsWith(fieldName) && !fieldName.equalsIgnoreCase(paramName) ? paramName.substring(fieldName.length() + 1) : null;
        try {
            return StringUtils.isEmpty(val) && fieldName.equalsIgnoreCase(paramName) ? EQUALS : DBFilterType.valueOf(val.toUpperCase(Locale.ENGLISH));
        } catch (Exception ex) {
            return null;
        }
    }

    public static Map<String, DBFilterType> asMap(DbField field) {
        final Map<String, DBFilterType> filters = new HashMap<>();
        boolean isTxt = (field.type() == DbFieldType.TEXT);
        Arrays.asList(DBFilterType.values()).forEach(v -> {
            if ((v.text() == isTxt || v.numeric() != isTxt)) {
                if (v == EQUALS) {
                    filters.put(field.name().toLowerCase(Locale.ENGLISH), v);
                } else {
                    filters.put(StringUtils.append(field.name(), "-", v.name()).toLowerCase(Locale.ENGLISH), v);
                }
            }
        });
        return filters;
    }

    private DBFilterType(boolean isText, boolean isNumeric) {
        this.isText = isText;
        this.isNumeric = isNumeric;
    }

    public boolean isMatch(String name, String match) {
        return this != EQUALS ? StringUtils.append(name, "-", this.name()).equalsIgnoreCase(match) : name.equalsIgnoreCase(match);
    }

    boolean numeric() {
        return this.isNumeric;
    }

    boolean text() {
        return this.isText;
    }

}
