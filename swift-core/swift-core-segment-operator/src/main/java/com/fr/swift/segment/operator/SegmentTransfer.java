package com.fr.swift.segment.operator;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.SegmentSource;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.operator.collate.segment.SegmentBuilder;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentLocationService SEG_LOCATION_SVC = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    private static final SwiftSegmentService SWIFT_SEG_SVC = SwiftContext.get().getBean(SwiftSegmentService.class);

    private static final SegmentService SEG_SVC = SwiftContext.get().getBean(SegmentService.class);


    protected SegmentKey realtSegKey;

    protected SegmentKey histSegKey;

    public SegmentTransfer(SegmentKey realtSegKey) {
        this.realtSegKey = realtSegKey;
        this.histSegKey = SWIFT_SEG_SVC.tryAppendSegment(realtSegKey.getTable(), StoreType.FINE_IO, SegmentSource.TRANSFERED);
    }

    /**
     * transfered segment
     *
     * @return
     */
    public SegmentKey transfer() {
        long start = System.currentTimeMillis();
        Segment realtSeg = null;
        Segment histSeg = null;
        try {
            realtSeg = newSegment(realtSegKey);
            histSeg = newSegment(histSegKey);
            SegmentBuilder segBuilder = new SegmentBuilder(histSeg, realtSeg.getMetaData().getFieldNames(), Collections.singletonList(realtSeg), Collections.singletonList(realtSeg.getAllShowIndex()));
            segBuilder.build();
            SegmentUtils.releaseHisSeg(Arrays.asList(realtSeg, histSeg));
            onSucceed(start, histSeg);
            return histSegKey;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed cost {}ms"
                    , realtSegKey, histSegKey, System.currentTimeMillis() - start);
            remove(histSegKey);
            throw e;
        }
    }

    private void onSucceed(long start, Segment histSeg) {
        SEG_LOCATION_SVC.saveOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(histSegKey));
        SEG_SVC.addSegment(histSegKey);
        SEG_SVC.removeSegment(realtSegKey);
        remove(realtSegKey);
        SwiftLoggers.getLogger().info("seg transferred from {} to {} cost {}ms with {} rows data"
                , realtSegKey, histSegKey, System.currentTimeMillis() - start, histSeg.getRowCount());
//        SwiftEventDispatcher.syncFire(SyncSegmentLocationEvent.REMOVE_SEG, Collections.singletonList(realtSegKey));
//        SwiftEventDispatcher.syncFire(SyncSegmentLocationEvent.PUSH_SEG, Collections.singletonList(histSegKey));
//        SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, histSegKey);
    }

    protected void remove(final SegmentKey segKey) {
        SEG_LOCATION_SVC.deleteOnNode(SwiftProperty.get().getMachineId(), Collections.singleton(segKey));
        SegmentUtils.clearSegment(segKey);
        SwiftLoggers.getLogger().info("seg {} removed", segKey);
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}