package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Where;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2019/1/22
 */
@SwiftService(name = "swiftDeleteService")
@SwiftBean(name = "swiftDeleteService")
public class SwiftDeleteService extends AbstractSwiftService implements DeleteService, Serializable {

    private static final long serialVersionUID = 1;

    private transient SwiftSegmentManager segmentManager;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;

        return true;
    }

    @Override
    public boolean delete(final SourceKey tableKey, final Where where) {
        boolean success = true;
        List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);

        for (SegmentKey segKey : segmentKeys) {
            if (!segmentManager.existsSegment(segKey)) {
                continue;
            }

            try {
                WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                ImmutableBitMap allShow = whereDeleter.delete(where);
                if (allShow.isEmpty()) {
                    SwiftContext.get().getBean(SwiftSegmentLocationService.class).delete(Collections.singleton(segKey));
                    SwiftContext.get().getBean(SwiftSegmentService.class).removeSegments(Collections.singletonList(segKey));
                    SegmentUtils.clearSegment(segKey);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                success = false;
            }
        }

        return success;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DELETE;
    }
}