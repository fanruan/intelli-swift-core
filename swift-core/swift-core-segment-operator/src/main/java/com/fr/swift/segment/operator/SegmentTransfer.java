package com.fr.swift.segment.operator;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.collate.segment.SegmentBuilder;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentLocationService SEG_LOCATION_SVC = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    protected SegmentKey realtSegKey;

    protected SegmentKey histSegKey;

    public SegmentTransfer(SegmentKey realtSegKey) {
        this.realtSegKey = realtSegKey;
        this.histSegKey = getHistorySegKey(realtSegKey);
    }

    private static SegmentKey getHistorySegKey(SegmentKey realtimeSegKey) {
        return new SwiftSegmentEntity(realtimeSegKey.getTable(), realtimeSegKey.getOrder(), StoreType.FINE_IO, realtimeSegKey.getSwiftSchema());
    }

    public void transfer() {
        if (!SEG_LOCATION_SVC.containsLocal(realtSegKey)) {
            return;
        }

        Segment realtSeg = null;
        Segment histSeg = null;
        try {
            SEG_SVC.addSegments(Collections.singletonList(histSegKey));

            realtSeg = newSegment(realtSegKey);
            histSeg = newSegment(histSegKey);
            SegmentBuilder segBuilder = new SegmentBuilder(histSeg, realtSeg.getMetaData().getFieldNames(), Collections.singletonList(realtSeg), Collections.singletonList(realtSeg.getAllShowIndex()));
            segBuilder.build();

            onSucceed();
        } catch (Exception e) {
            remove(histSegKey);
            SwiftLoggers.getLogger().error("seg transfer from {} to {} failed", realtSegKey, histSegKey, e);
        } finally {
            SegmentUtils.releaseHisSeg(Arrays.asList(realtSeg, histSeg));
        }
    }

    private void onSucceed() {
        remove(realtSegKey);
        SEG_LOCATION_SVC.saveOrUpdateLocal(Collections.singleton(histSegKey));
        SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegment(histSegKey);
        SwiftLoggers.getLogger().info("seg transferred from {} to {}", realtSegKey, histSegKey);

        SwiftEventDispatcher.syncFire(SyncSegmentLocationEvent.REMOVE_SEG, Collections.singletonList(realtSegKey));
        SwiftEventDispatcher.syncFire(SyncSegmentLocationEvent.PUSH_SEG, Collections.singletonList(histSegKey));
        SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, histSegKey);
    }

    protected void remove(final SegmentKey segKey) {
        SEG_LOCATION_SVC.delete(Collections.singleton(segKey));
        SEG_SVC.removeSegments(Collections.singletonList(segKey));
        SegmentUtils.clearSegment(segKey);
        SwiftLoggers.getLogger().info("seg {} removed", segKey);
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}