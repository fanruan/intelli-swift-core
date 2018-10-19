package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Inserter;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    protected SegmentKey oldSegKey, newSegKey;

    private boolean index;

    public SegmentTransfer(SegmentKey oldSegKey, SegmentKey newSegKey) {
        this(oldSegKey, newSegKey, true);
    }

    public SegmentTransfer(SegmentKey oldSegKey, SegmentKey newSegKey, boolean index) {
        this.oldSegKey = oldSegKey;
        this.newSegKey = newSegKey;
        this.index = index;
    }

    public void transfer() {
        Segment oldSeg = newSegment(oldSegKey), newSeg = newSegment(newSegKey);
        Inserter inserter = (Inserter) SwiftContext.get().getBean("inserter", newSeg);
        SegmentResultSet swiftResultSet = null;
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            swiftResultSet = new SegmentResultSet(oldSeg);
            inserter.insertData(swiftResultSet);

            indexSegmentIfNeed(newSeg);

            onSucceed();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("segment transfer from {} to {} failed: {}", oldSegKey, newSegKey, e);
            remove(newSegKey);
        } finally {
            if (swiftResultSet != null) {
                swiftResultSet.close();
            }
        }
    }

    protected void onSucceed() {
        remove(oldSegKey);
        SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(newSegKey);
    }

    private void indexSegmentIfNeed(Segment newSeg) throws Exception {
        SegmentUtils.indexSegmentIfNeed(Collections.singletonList(newSeg));
    }

    private void remove(final SegmentKey segKey) {
        SEG_SVC.removeSegments(Collections.singletonList(segKey));
        SegmentUtils.clearSegment(segKey);
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}