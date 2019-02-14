package com.fr.swift.query.post.meta;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

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
            case FUNNEL:
                return new FunnelMetaCreator();
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
