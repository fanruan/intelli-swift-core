package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/3/4
 *
 * @author Lucifer
 * @description extends各种service，避免接口不同步
 */
public interface ServiceContext {

    //analyse service methods
    @InvokeMethod(value = QueryableProcessHandler.class, target = Target.ANALYSE)
    QueryResultSet getQueryResult(String queryJson) throws Exception;

    //collate service methods
    void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;

    boolean delete(SourceKey tableKey, Where where) throws Exception;

    void clearQuery(String queryId);
}
