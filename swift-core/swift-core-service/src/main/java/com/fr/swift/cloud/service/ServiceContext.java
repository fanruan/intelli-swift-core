package com.fr.swift.cloud.service;

import com.fr.swift.cloud.basics.annotation.InvokeMethod;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.basics.handler.DeleteProcessHandler;
import com.fr.swift.cloud.basics.handler.DetailQueryableProcessHandler;
import com.fr.swift.cloud.basics.handler.MasterProcessHandler;
import com.fr.swift.cloud.basics.handler.MigrateAsyncHandler;
import com.fr.swift.cloud.basics.handler.MigrateSyncHandler;
import com.fr.swift.cloud.basics.handler.QueryableProcessHandler;
import com.fr.swift.cloud.basics.handler.TaskProcessHandler;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.service.event.NodeEvent;
import com.fr.swift.cloud.service.event.NodeMessage;
import com.fr.swift.cloud.source.SourceKey;

import java.util.List;

/**
 * This class created on 2019/3/4
 *
 * @author Lucifer
 * @description extends各种service，避免接口不同步
 */
public interface ServiceContext extends SwiftService {

    @InvokeMethod(value = QueryableProcessHandler.class, target = Target.ANALYSE)
    QueryResultSet getQueryResult(String queryJson) throws Exception;

    @InvokeMethod(value = DetailQueryableProcessHandler.class, target = Target.ANALYSE)
    SwiftResultSet getResultResult(String queryJson) throws Exception;

    void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception;

    @InvokeMethod(value = DeleteProcessHandler.class, target = Target.ALL)
    boolean delete(SourceKey tableKey, Where where) throws Exception;

    void clearQuery(String queryId);

    void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception;

    @InvokeMethod(value = TaskProcessHandler.class, target = Target.ALL)
    boolean dispatch(String taskBean, String location) throws Exception;

    // following 3 interfaces is for migrate
    @InvokeMethod(value = MasterProcessHandler.class, target = Target.ALL)
    boolean report(NodeEvent nodeEvent, NodeMessage nodeMessage);

    @InvokeMethod(value = MigrateAsyncHandler.class, target = Target.MIGRATE)
    boolean deleteFiles(String targetPath, String clusterId);

    @InvokeMethod(value = MigrateSyncHandler.class, target = Target.MIGRATE)
    boolean updateConfigs(List<SegmentKey> segmentKeys, String clusterId);
}
