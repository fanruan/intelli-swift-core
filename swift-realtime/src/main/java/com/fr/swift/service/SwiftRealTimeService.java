package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.segment.recover.SwiftSegmentRecovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.io.Serializable;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 */
public class SwiftRealTimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    public SwiftRealTimeService(String id) {
        super(id);
    }

    public SwiftRealTimeService() {
    }

    @Override
    public void insert(SourceKey tableKey, SwiftResultSet resultSet) {

    }

    @Override
    public void merge(List<SourceKey> tableKeys) {

    }

    @Override
    public void recover(List<SourceKey> tableKeys) {

    }

    @Override
    public <T extends SwiftResultSet> T query(QueryInfo<T> queryInfo) {
        // TODO: 2018/6/4  
        return null;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean succ = true;
        succ &= super.start();
        succ &= recover0();
        return succ;
    }

    private static boolean recover0() {
        try {
            // 恢复所有realtime块
            SwiftSegmentRecovery.getInstance().recoverAll();
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }
}