package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.SwiftService;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.exception.SwiftServiceException;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.cache.QueryCacheBuilder;
import com.fr.swift.cloud.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.io.Serializable;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@SwiftService(name = "analyse")
@SwiftBean(name = "analyse")
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService, Serializable {
    private static final long serialVersionUID = 841582089735823794L;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        try {
            cacheSegments();
        } catch (SwiftMetaDataException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void cacheSegments() throws SwiftMetaDataException {
        SwiftSegmentService segmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);
        SegmentService manager = SwiftContext.get().getBean(SegmentService.class);
        for (SwiftMetaData metadata : metaDataService.getAllMetas()) {
            List<SegmentKey> segmentKeys = segmentService.getOwnSegments(new SourceKey(metadata.getTableName()));
            for (SegmentKey segmentKey : segmentKeys) {
                manager.getSegment(segmentKey);
            }
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        return super.shutdown();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public QueryResultSet getQueryResult(String queryJson) throws Exception {
        SwiftLoggers.getLogger().debug(queryJson);
        QueryBean info = QueryBeanFactory.create(queryJson);
        return QueryCacheBuilder.builder().getQueryResultSetCache(info).getQueryResultSet();
    }

    @Override
    public SwiftResultSet getResultResult(String queryJson) throws Exception {
        DetailQueryInfoBean info = (DetailQueryInfoBean) QueryBeanFactory.create(queryJson);
        return QueryCacheBuilder.builder().getCalcResultSetCache(info).getQueryCalcPage();
    }
}