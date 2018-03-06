package com.fr.swift.cal.info;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.filter.info.FilterInfo;

/**
 * @author anchore
 * @date 2018/3/6
 */
public class StringControlQueryInfo extends AbstractQueryInfo {
    int clickMore;

    public StringControlQueryInfo(Cursor cursor, String id, FilterInfo filterInfo) {
        super(cursor, id, filterInfo);
    }

    @Override
    public QueryType getType() {
        return QueryType.STRING_CONTROL;
    }
}