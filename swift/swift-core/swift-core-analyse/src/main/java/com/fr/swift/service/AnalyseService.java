package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunner;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SwiftResultSet;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/13
 */
public interface AnalyseService extends QueryRunner, SwiftService, Serializable {
    @Override
    SwiftResultSet getQueryResult(QueryBean info) throws Exception;

    /**
     * 远程查询
     *
     * @param jsonString
     * @param remoteURI
     * @return
     * @throws SQLException
     */
    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    SwiftResultSet getRemoteQueryResult(String jsonString, SegmentDestination remoteURI) throws SQLException;

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType);

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    void removeTable(String cluster, String sourceKey);

    @InvokeMethod(value = CommonProcessHandler.class, target = Target.ANALYSE)
    void removeSegments(String clusterId, String sourceKey, List<String> segmentKeys);
}
