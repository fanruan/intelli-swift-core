package com.fr.swift.service;

import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.SwiftSegmentInfo;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public final class SwiftAnalyseService extends AbstractSwiftService implements QueryRunner {

    private static SwiftAnalyseService analyseService;

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start =  super.start();
        analyseService = this;
        return start;
    }

    /**
     * 更新segment信息
     *
     * @param info
     */
    public void updateSegmentInfo(SwiftSegmentInfo info) {
        // TODO: 2018/5/28
    }

    /**
     * 获取segment信息用于buildQuery
     *
     * @return
     */
    public SwiftSegmentManager getSwiftSegmentManager() {
        // TODO: 2018/5/28
        return LocalSegmentProvider.getInstance();
    }

    public static SwiftAnalyseService getInstance() {
        return analyseService;
    }

    @Override
    public <T extends SwiftResultSet> T executeQuery(QueryInfo<T> info) throws SQLException {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }
}
