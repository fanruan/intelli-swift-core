package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.QueryInfo;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public class SwiftAnalyseService extends AbstractSwiftService implements QueryRunner, SegmentLocationManager {
    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start =  super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        return start;
    }

    @Override
    public <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }

    @Override
    public List<URI> getSegmentLocationURI(SourceKey table) {
        return SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo);
    }
}
