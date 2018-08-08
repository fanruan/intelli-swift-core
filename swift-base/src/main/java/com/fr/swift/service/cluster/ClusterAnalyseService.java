package com.fr.swift.service.cluster;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/8/7
 */
public interface ClusterAnalyseService extends AnalyseService {
    /**
     * 远程查询
     *
     * @param jsonString
     * @param remoteURI
     * @return
     * @throws SQLException
     */
    SwiftResultSet getRemoteQueryResult(String jsonString, SegmentDestination remoteURI) throws SQLException;
}
