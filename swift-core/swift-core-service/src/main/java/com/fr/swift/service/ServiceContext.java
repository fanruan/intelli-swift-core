package com.fr.swift.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.DeleteProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.basics.handler.MigrateProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.basics.handler.TaskProcessHandler;
import com.fr.swift.db.Where;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/3/4
 *
 * @author Lucifer
 * @description extends各种service，避免接口不同步
 */
public interface ServiceContext extends SwiftService {

    @InvokeMethod(value = QueryableProcessHandler.class, target = Target.ANALYSE)
    QueryResultSet getQueryResult(String queryJson) throws Exception;

    void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;

    @InvokeMethod(value = DeleteProcessHandler.class, target = Target.ALL)
    boolean delete(SourceKey tableKey, Where where) throws Exception;

    void clearQuery(String queryId);

    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    @InvokeMethod(value = MigrateProcessHandler.class, target = Target.MIGRATE)
    boolean migrate(Map<SegmentKey, Map<String, byte[]>> segments, String location);

    @InvokeMethod(value = TaskProcessHandler.class, target = Target.ALL)
    boolean dispatch(String task, String location) throws Exception;

    @InvokeMethod(value = MasterProcessHandler.class, target = Target.ALL)
    void report(NodeEvent nodeEvent, NodeMessage nodeMessage);
}
