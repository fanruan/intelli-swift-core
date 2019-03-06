package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CollateProcessHandler;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.IndexProcessHandler;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.basics.handler.StatusProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.db.Where;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.stuff.IndexingStuff;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/3/4
 *
 * @author Lucifer
 * @description
 */
public interface ServiceContext {

    //base service methods
    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ALL)
    void cleanMetaCache(String[] sourceKeys);

    //analyse service methods
    @InvokeMethod(value = QueryableProcessHandler.class, target = Target.ANALYSE)
    QueryResultSet getQueryResult(String queryJson) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType);

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    void removeSegments(String clusterId, SourceKey sourceKey, List<String> segmentKeys);


    //realtime service methods
    @InvokeMethod(value = InsertSegmentProcessHandler.class, target = Target.REAL_TIME)
    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    //history service methods
    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.HISTORY, Target.REAL_TIME})
    void truncate(SourceKey tableKey) throws SQLException;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.HISTORY)
    void removeHistory(List<SegmentKey> needRemoveList);

    //index service methods
    @InvokeMethod(IndexProcessHandler.class)
    <Stuff extends IndexingStuff> void index(Stuff stuff);

    @InvokeMethod(StatusProcessHandler.class)
    ServerCurrentStatus currentStatus();


    //collate service methods
    @InvokeMethod(value = CollateProcessHandler.class)
    void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;


    //delete service methods
    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    boolean delete(SourceKey tableKey, Where where) throws Exception;


    //upload service methods
    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void upload(Set<SegmentKey> segKeys) throws Exception;

    @InvokeMethod(value = SyncDataProcessHandler.class, target = Target.HISTORY)
    void download(Set<SegmentKey> segKeys, boolean replace) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void uploadAllShow(Set<SegmentKey> segKeys) throws Exception;

    @InvokeMethod(value = CommonProcessHandler.class, target = {Target.REAL_TIME, Target.HISTORY})
    void downloadAllShow(Set<SegmentKey> segKeys) throws Exception;
}
