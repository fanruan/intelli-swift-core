package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.cache.QueryCacheBuilder;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

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
        return QueryCacheBuilder.builder().getOrBuildCache(info).getQueryResultSet();
    }
}