package com.fr.swift.service;

import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.cal.info.QueryInfo;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public class SwiftAnalyseService extends AbstractSwiftService implements QueryRunner, SegmentLocationManager, Serializable {

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
    public Set<URI> getSegmentLocationURI(SourceKey table) {
        return SegmentLocationProvider.getInstance().getSegmentLocaltionURI(table);
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo);
    }
}
