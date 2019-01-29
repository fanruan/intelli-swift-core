package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SourceKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/28
 */
abstract class BaseSegmentLocationListener implements SwiftEventListener<List<SegmentKey>> {

    @Override
    public void on(List<SegmentKey> segKeys) {
        if (!SwiftProperty.getProperty().isCluster()) {
            return;
        }

        Map<SourceKey, List<SegmentDestination>> realtimeSegDsts = new HashMap<SourceKey, List<SegmentDestination>>(),
                historySegDsts = new HashMap<SourceKey, List<SegmentDestination>>();

        for (SegmentKey segKey : segKeys) {
            SourceKey tableKey = segKey.getTable();

            if (segKey.getStoreType().isTransient()) {
                SegmentDestination segDst = new RealTimeSegDestImpl(SwiftProperty.getProperty().getClusterId(), segKey.getId(), segKey.getOrder());
                if (!realtimeSegDsts.containsKey(tableKey)) {
                    realtimeSegDsts.put(tableKey, new ArrayList<SegmentDestination>());
                }
                realtimeSegDsts.get(tableKey).add(segDst);
            } else {
                SegmentDestination segDst = new SegmentDestinationImpl(SwiftProperty.getProperty().getClusterId(), segKey.getId(), segKey.getOrder());
                if (!historySegDsts.containsKey(tableKey)) {
                    historySegDsts.put(tableKey, new ArrayList<SegmentDestination>());
                }
                historySegDsts.get(tableKey).add(segDst);
            }
        }

        // 传空会删所有
        if (!historySegDsts.isEmpty()) {
            trigger(new SegmentLocationInfoImpl(ServiceType.HISTORY, historySegDsts));
        }
        if (!realtimeSegDsts.isEmpty()) {
            trigger(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, realtimeSegDsts));
        }
    }

    abstract Serializable trigger(SegmentLocationInfo segLocations);
}