package com.fr.swift.service.handler.analyse;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@SwiftBean
public class SwiftAnalyseEventHandler extends AbstractHandler<AbstractAnalyseRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseEventHandler.class);

    @Override
    public <S extends Serializable> S handle(AbstractAnalyseRpcEvent event) {
        switch (event.subEvent()) {
            case SEGMENT_LOCATION:
                Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair = (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>) event.getContent();
                SegmentLocationInfoContainer.getContainer().add(pair);
                AnalyseService analyseService = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
                analyseService.updateSegmentInfo(pair.getValue(), pair.getKey());
                break;
            case REQUEST_SEG_LOCATION:
                String clusterId = (String) event.getContent();
                Map<String, ClusterEntity> analyseNodeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                if (analyseNodeMap.containsKey(clusterId)) {
                    return (S) SegmentLocationInfoContainer.getContainer().getLocationInfo();
                }
                return (S) Collections.emptyList();
            default:
                break;
        }
        return null;
    }
}
