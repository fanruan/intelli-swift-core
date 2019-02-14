package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.operator.Inserter;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    protected SegmentKey oldSegKey;

    protected SegmentKey newSegKey;

    public SegmentTransfer(SegmentKey oldSegKey, SegmentKey newSegKey) {
        this.oldSegKey = oldSegKey;
        this.newSegKey = newSegKey;
    }

    public void transfer() {
        if (!SEG_SVC.containsSegment(oldSegKey)) {
            return;
        }

        final Segment oldSeg = newSegment(oldSegKey), newSeg = newSegment(newSegKey);
        Inserter inserter = SwiftContext.get().getBean("inserter", Inserter.class, newSeg);
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            inserter.insertData(new SegmentResultSet(oldSeg));

            indexSegmentIfNeed(newSeg);
            onSucceed();

            SwiftLoggers.getLogger().info("seg transferred from {} to {}", oldSegKey, newSegKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed", oldSegKey, newSegKey, e);
            remove(newSegKey);
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
        SwiftLoggers.getLogger().info("seg {} removed", segKey);
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}