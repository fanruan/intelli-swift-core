package com.fr.swift.cloud.query.post.meta;

import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.Crasher;

/**
 * Created by Lyon on 2018/6/1.
 */
public class SwiftMetaDataUtils {

    public static MetaDataCreator create(QueryType type) {
        switch (type) {
            case GROUP:
                return new GroupMetaDataCreator();
            case DETAIL:
                return new DetailMetaDataCreator();
            default:
                // do nothing
        }
        return Crasher.crash(new UnsupportedOperationException("Unsupported query type!"));
    }

    public static SwiftMetaData createMetaData(QueryBean bean) throws SwiftMetaDataException {
        QueryType type = bean.getQueryType();
        return create(type).create(bean);
    }
}
