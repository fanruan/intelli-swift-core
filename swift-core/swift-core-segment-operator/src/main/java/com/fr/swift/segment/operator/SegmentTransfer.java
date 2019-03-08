package com.fr.swift.segment.operator;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.collate.segment.SegmentBuilder;

import java.util.Arrays;
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

        Segment oldSeg = null;
        Segment newSeg = null;
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            oldSeg = newSegment(oldSegKey);
            newSeg = newSegment(newSegKey);
            SegmentBuilder segBuilder = new SegmentBuilder(newSeg, oldSeg.getMetaData().getFieldNames(), Collections.singletonList(oldSeg), Collections.singletonList(oldSeg.getAllShowIndex()));
            segBuilder.build();

            onSucceed();
        } catch (Exception e) {
            remove(newSegKey);
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed", oldSegKey, newSegKey, e);
        } finally {
            SegmentUtils.releaseHisSeg(Arrays.asList(oldSeg, newSeg));
        }
    }

    protected void onSucceed() {
        remove(oldSegKey);
        SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(newSegKey);
        SwiftLoggers.getLogger().info("seg transferred from {} to {}", oldSegKey, newSegKey);
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