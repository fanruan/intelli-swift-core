package com.fr.swift.service.handler.history;

import com.fr.stable.StringUtils;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.service.DataSyncRuleService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.SegmentLoadRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.structure.Pair;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager extends AbstractHandler<SegmentLoadRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HistoryDataSyncManager.class);

    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;

    @Override
    public <S extends Serializable> S handle(SegmentLoadRpcEvent event) {

        Map<String, List<SegmentKey>> allSegments = clusterSegmentService.getAllSegments();
        Set<SegmentKey> needLoadSegments = new HashSet<SegmentKey>();
        SegmentLocationInfo.UpdateType updateType;
        String needLoadSourceKey;
        String sourceClusterId = event.getSourceClusterId();

        switch (event.subEvent()) {
            case LOAD_SEGMENT:
                updateType = SegmentLocationInfo.UpdateType.ALL;
                needLoadSourceKey = (String) event.getContent();
                if (StringUtils.isNotEmpty(needLoadSourceKey)) {
                    List<SegmentKey> keys = allSegments.get(needLoadSourceKey);
                    needLoadSegments.addAll(keys);
                } else {
                    for (Map.Entry<String, List<SegmentKey>> segEntry : allSegments.entrySet()) {
                        needLoadSegments.addAll(segEntry.getValue());
                    }
                }
                break;
            case TRANS_COLLATE_LOAD:
                updateType = SegmentLocationInfo.UpdateType.PART;
                Pair<String, List<String>> content = (Pair<String, List<String>>) event.getContent();
                needLoadSourceKey = content.getKey();
                List<String> contentSegmentKeys = content.getValue();
                if (StringUtils.isNotEmpty(needLoadSourceKey)) {
                    List<SegmentKey> allSegmentKeys = allSegments.get(needLoadSourceKey);
                    List<SegmentKey> target = new ArrayList<SegmentKey>();
                    for (SegmentKey segmentKey : allSegmentKeys) {
                        if (contentSegmentKeys.contains(segmentKey.toString())) {
                            target.add(segmentKey);
                        }
                    }
                    needLoadSegments.addAll(target);
                } else {
                    return null;
                }
                break;
            default:
                return null;
        }
        return (S) dealNeedLoadSegments(filterHistorySegments(needLoadSegments), updateType, sourceClusterId);
    }


    private EventResult dealNeedLoadSegments(Set<SegmentKey> needLoadSegments,
                                             SegmentLocationInfo.UpdateType updateType,
                                             String sourceClusterId) {
        EventResult eventResult = new EventResult();
        try {
            eventResult.setClusterId(sourceClusterId);
            HistoryService service = ProxySelector.getInstance().getFactory().getProxy(HistoryService.class);
            service.load(needLoadSegments, updateType == SegmentLocationInfo.UpdateType.ALL);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            eventResult.setError(e.getMessage());
        }
        return eventResult;
    }

    private Set<SegmentKey> filterHistorySegments(Set<SegmentKey> source) {
        Set<SegmentKey> result = new HashSet<SegmentKey>();
        for (SegmentKey segmentKey : source) {
            if (segmentKey.getStoreType().isPersistent()) {
                result.add(segmentKey);
            }
        }
        return result;
    }
}
