package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;

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

    private transient SwiftSegmentManager segmentManager;

    private transient SwiftSegmentService segmentService;

    private transient SwiftSegmentLocationService segLocationSvc;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean(SwiftSegmentManager.class);
        segmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        segmentService = null;
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
