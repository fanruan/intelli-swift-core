package com.fr.swift.result;


import com.fr.swift.result.qrs.DSType;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SwiftResultSetUtils {
    /**
     * TODO 2018/12/12 实现
     *
     * @param queryResultSet
     * @return
     */
    public static SwiftResultSet toSwiftResultSet(QueryResultSet queryResultSet) {
        DSType type = queryResultSet.type();
        switch (type) {
            case ROW:
                return null;
            case NODE:
                return null;
            default:
                return null;
        }
    }
}
