package com.fr.swift.segment;

import com.fr.swift.segment.impl.SegmentLocationManagerImpl;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
public class SegmentLocationProvider {
    private static SegmentLocationProvider ourInstance = new SegmentLocationProvider();

    public static SegmentLocationProvider getInstance() {
        return ourInstance;
    }

    private SegmentLocationManager historyManager = new SegmentLocationManagerImpl();
    private SegmentLocationManager realTimeManager = new SegmentLocationManagerImpl();

    private SegmentLocationProvider() {
    }

    /**
     * 给AnalysisService调用并更新segment location信息
     *
     * @param locationInfo
     */
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        switch (locationInfo.serviceType()) {
            case HISTORY:
                historyManager.updateSegmentInfo(locationInfo, updateType);
                break;
            case REAL_TIME:
                realTimeManager.updateSegmentInfo(locationInfo, updateType);
                break;
            default:
                // do nothing
        }
    }

    public List<SegmentDestination> getRealTimeSegmentLocation(SourceKey table) {
        // TODO: 2018/5/30
        return realTimeManager.getSegmentLocationURI(table);
    }

    public List<SegmentDestination> getHistorySegmentLocation(SourceKey table) {
        return historyManager.getSegmentLocationURI(table);
    }
}
