package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.recover.SwiftSegmentRecovery;
import com.fr.swift.source.SerializableResultSet;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author pony
 * @date 2017/10/10
 */
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    @Override
    public void insert(SourceKey tableKey, SwiftResultSet resultSet) throws SQLException {
        SwiftLoggers.getLogger().info("insert");

        new Incrementer(tableKey).increment(resultSet);
    }

    @Override
    public void merge(List<SegmentKey> tableKeys) {
        SwiftLoggers.getLogger().info("merge");
    }

    @Override
    public void recover(List<SegmentKey> tableKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    @Override
    public SerializableResultSet query(QueryInfo queryInfo) throws SQLException {
        SwiftResultSet resultSet = QueryBuilder.buildQuery(queryInfo).getQueryResult();
        return new SerializableResultSet(resultSet);
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();

        recover0();

        return true;
    }

    private static void recover0() {
        CommonExecutor.get().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    // 恢复所有realtime块
                    SwiftSegmentRecovery.getInstance().recoverAll();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    private static final long serialVersionUID = 4719723736240190155L;

    public SwiftRealtimeService(String id) {
        super(id);
    }

    public SwiftRealtimeService() {
    }
}