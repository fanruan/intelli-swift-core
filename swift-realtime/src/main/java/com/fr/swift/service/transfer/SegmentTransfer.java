package com.fr.swift.service.transfer;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.selector.ClusterSelector;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private SegmentKey oldSegKey;
    private SegmentKey newSegKey;

    public SegmentTransfer(SegmentKey oldSegKey) {
        this.oldSegKey = oldSegKey;
        this.newSegKey = getHistorySegKey(oldSegKey);
    }

    private static SegmentKey getHistorySegKey(SegmentKey realtimeSegKey) {
        return new SwiftSegmentEntity(realtimeSegKey.getTable(), realtimeSegKey.getOrder(), StoreType.FINE_IO, realtimeSegKey.getSwiftSchema());
    }

    public void transfer() {
        if (!SEG_SVC.containsSegment(oldSegKey)) {
            return;
        }

        final Segment oldSeg = SegmentUtils.newSegment(oldSegKey), newSeg = SegmentUtils.newSegment(newSegKey);
        Inserter inserter = (Inserter) SwiftContext.get().getBean("inserter", newSeg);
        SegmentResultSet swiftResultSet = null;
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            swiftResultSet = new SegmentResultSet(oldSeg);
            inserter.insertData(swiftResultSet);

            indexSegmentIfNeed(newSeg);
            onSucceed();

            SegmentContainer.NORMAL.register(newSegKey);

            SwiftLoggers.getLogger().info("seg transferred from {} to {}", oldSegKey, newSegKey);
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed", oldSegKey, newSegKey, e);
            onFailed();
        } finally {
            if (swiftResultSet != null) {
                swiftResultSet.close();
            }
        }
    }

    private void onSucceed() {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            SwiftEventDispatcher.fire(SegmentEvent.UPLOAD_HISTORY, newSegKey);
        } else {
            remove(oldSegKey);
            SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(newSegKey);
        }
    }

    private void indexSegmentIfNeed(Segment newSeg) throws Exception {
        SegmentUtils.indexSegmentIfNeed(Collections.singletonList(newSeg));
    }

    private void onFailed() {
        remove(oldSegKey);
        remove(newSegKey);
    }

    private void remove(final SegmentKey segKey) {
        SEG_SVC.removeSegments(Collections.singletonList(segKey));
        SegmentUtils.clearSegment(segKey);
        SwiftLoggers.getLogger().info("seg {} removed", segKey);
    }
}