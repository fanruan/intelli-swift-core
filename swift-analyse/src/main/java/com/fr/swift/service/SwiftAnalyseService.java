package com.fr.swift.service;

import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public class SwiftAnalyseService extends AbstractSwiftService implements QueryRunner{
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
}
