package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.recover.SwiftSegmentRecovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

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
    public void merge(List<SourceKey> tableKeys) {
        SwiftLoggers.getLogger().info("merge");
    }

    @Override
    public void recover(List<SourceKey> tableKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    @Override
    public <T extends SwiftResultSet> T query(QueryInfo<T> queryInfo) throws SQLException {
        return QueryBuilder.buildQuery(queryInfo).getQueryResult();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean succ = true;
        succ &= super.start();

//        FRProxyCache.registerInstance(RealtimeService.class, this);

        succ &= recover0();
        return succ;
    }

    private static boolean recover0() {
        try {
            // 恢复所有realtime块
            SwiftSegmentRecovery.getInstance().recoverAll();
            return true;
        } catch (Exception e) {
//            SwiftLoggers.getLogger().error(e);
            return false;
        }
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