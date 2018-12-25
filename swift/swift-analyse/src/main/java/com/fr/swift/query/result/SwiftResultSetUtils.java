package com.fr.swift.query.result;


import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

/**
 * @author yee
 * @date 2018-12-12
 */
public class SwiftResultSetUtils {

    public static SwiftResultSet toSwiftResultSet(QueryResultSet queryResultSet, QueryBean bean) {
        SwiftMetaData metaData;
        try {
            metaData = SwiftMetaDataUtils.createMetaData(bean);
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
        return queryResultSet.convert(metaData);
    }
}
