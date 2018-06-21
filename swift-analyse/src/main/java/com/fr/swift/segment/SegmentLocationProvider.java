package com.fr.swift.segment;

import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationManagerImpl;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
public class SegmentLocationProvider implements SegmentLocationManager {
    private static SegmentLocationProvider ourInstance = new SegmentLocationProvider();

    public static SegmentLocationProvider getInstance() {
        return ourInstance;
    }

    private SegmentLocationManager historyManager = new SegmentLocationManagerImpl();
    private SegmentLocationManager realTimeManager = new SegmentLocationManagerImpl();

    private SegmentLocationProvider() {
    }

    @Override
    public List<SegmentDestination> getSegmentLocationURI(SourceKey table) {
        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>(getRealTimeSegmentLocation(table));
        destinations.addAll(getHistorySegmentLocation(table));
        // tmp
        destinations.add(new SegmentDestinationImpl("192.168.2.1:7000", URI.create(""), 0, null, "historyQuery"));
        return Collections.unmodifiableList(destinations);
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

    private List<SegmentDestination> getRealTimeSegmentLocation(SourceKey table) {
        // TODO: 2018/5/30
        return realTimeManager.getSegmentLocationURI(table);
    }

    private List<SegmentDestination> getHistorySegmentLocation(SourceKey table) {
        return historyManager.getSegmentLocationURI(table);
    }
}
