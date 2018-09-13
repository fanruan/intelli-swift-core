package com.fr.swift.segment;

import com.fr.swift.segment.impl.SegmentLocationManagerImpl;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        // 这行是为了模拟远程特意加的，，，，，
//        destinations.add(new SegmentDestinationImpl("192.168.2.1:7000", URI.create(""), 0, null, "historyQuery"));
        return Collections.unmodifiableList(destinations);
    }

    /**
     * 给AnalysisService调用并更新segment location信息
     *
     * @param locationInfo
     */
    @Override
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

    @Override
    public Map<String, List<SegmentDestination>> getSegmentInfo() {
        return null;
    }

    @Override
    public void removeTable(String sourceKey) {
        historyManager.removeTable(sourceKey);
        realTimeManager.removeTable(sourceKey);
    }

    @Override
    public void removeSegment(String sourceKey, List<String> segmentKeys) {
        historyManager.removeSegment(sourceKey, segmentKeys);
        realTimeManager.removeSegment(sourceKey, segmentKeys);
    }

    public Map<String, List<SegmentDestination>> getSegmentInfo(ServiceType serviceType) {
        switch (serviceType) {
            case HISTORY:
                return historyManager.getSegmentInfo();
            case REAL_TIME:
                return realTimeManager.getSegmentInfo();
            default:
                return Collections.emptyMap();
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
