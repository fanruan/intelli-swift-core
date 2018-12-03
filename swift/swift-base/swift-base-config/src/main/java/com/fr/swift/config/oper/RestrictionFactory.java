package com.fr.swift.config.oper;

import java.util.Collection;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface RestrictionFactory {
    Object eq(String columnName, Object serializable);

    Object in(String columnName, Collection collection);

    Object like(String columnName, String value, MatchMode matchMode);

    enum MatchMode {
        //
        EXACT,
        START,
        END,
        ANYWHERE
    }
}
