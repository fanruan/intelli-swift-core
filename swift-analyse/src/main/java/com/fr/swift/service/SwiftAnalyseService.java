package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {

    private static final long serialVersionUID = 841582089735823794L;

    public SwiftAnalyseService(String id) {
        super(id);
    }

    public SwiftAnalyseService() {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        return start;
    }

    @Override
    public <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }

    @Override
    public <T extends SwiftResultSet> T getRemoteQueryResult(QueryInfo<T> info, SegmentDestination remoteURI) {
        // TODO: 2018/5/31 远程调用history或者realTime节点上的服务进行查询
        String node = remoteURI.getNode();

        return null;
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }
}
