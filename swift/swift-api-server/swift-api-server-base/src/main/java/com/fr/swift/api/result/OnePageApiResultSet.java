package com.fr.swift.api.result;

import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2019-01-14
 */
public class OnePageApiResultSet extends BaseApiResultSet {
    private static final long serialVersionUID = -2812866200853256669L;

    public OnePageApiResultSet(Object queryObject, SwiftMetaData metaData, List list, int rowCount, boolean originHasNextPage) {
        super(queryObject, metaData, list, rowCount, originHasNextPage);
    }

    @Override
    public SwiftApiResultSet queryNextPage(Object queryInfo) throws SQLException {
        return null;
    }
}
