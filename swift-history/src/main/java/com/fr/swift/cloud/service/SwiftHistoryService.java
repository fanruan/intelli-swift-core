package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.SwiftService;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.service.SwiftSegmentLocationService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.exception.SwiftServiceException;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.source.SourceKey;

import java.io.Serializable;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "history")
@SwiftBean(name = "history")
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {
    private static final long serialVersionUID = -6013675740141588108L;

    private transient SegmentService segmentService;

    private transient SwiftSegmentService swiftSegmentService;

    private transient SwiftSegmentLocationService segLocationSvc;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentService = SwiftContext.get().getBean(SegmentService.class);
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentService = null;
        swiftSegmentService = null;
        segLocationSvc = null;
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    public void removeHistory(List<SegmentKey> needRemoveList) {
        for (SegmentKey segmentKey : needRemoveList) {
            if (segmentKey.getStoreType().isPersistent()) {
                SegmentUtils.clearSegment(segmentKey);
            }
        }
    }

    @Override
    public void truncate(SourceKey tableKey) {
    }
}
