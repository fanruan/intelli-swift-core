package com.fr.swift.api.rpc.session;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface SwiftPublicApiSession extends SwiftApiSession {
    /**
     * 根据QueryBean查询
     *
     * @param database
     * @param queryBean
     * @return
     * @throws SwiftMetaDataAbsentException
     */
    SwiftResultSet query(SwiftDatabase database, QueryBean queryBean) throws SwiftMetaDataAbsentException;
}
