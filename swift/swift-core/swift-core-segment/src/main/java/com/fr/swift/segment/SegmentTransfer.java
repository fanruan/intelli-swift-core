package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.util.IoUtil;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private SegmentKey oldSegKey;
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
        Inserter inserter = (Inserter) SwiftContext.get().getBean("inserter", newSeg);
        SegmentResultSet swiftResultSet = null;
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            swiftResultSet = new SegmentResultSet(oldSeg);
            inserter.importData(swiftResultSet);
            IoUtil.release(inserter);
            indexSegmentIfNeed(newSeg);
            onSucceed();
            SegmentContainer.NORMAL.updateSegment(newSegKey, newSeg);

            SwiftLoggers.getLogger().info("seg transferred from {} to {}", oldSegKey, newSegKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed", oldSegKey, newSegKey, e);
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
        SwiftLoggers.getLogger().info("seg {} removed", segKey);
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}