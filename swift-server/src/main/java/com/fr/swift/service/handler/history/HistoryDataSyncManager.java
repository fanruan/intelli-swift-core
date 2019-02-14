package com.fr.swift.service.handler.history;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.SegmentLoadRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.UploadService;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

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
@SwiftBean
public class HistoryDataSyncManager extends AbstractHandler<SegmentLoadRpcEvent> {

    private SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    @Override
    public <S extends Serializable> S handle(SegmentLoadRpcEvent event) {

        Map<SourceKey, List<SegmentKey>> allSegments = clusterSegmentService.getAllSegments();
        Set<SegmentKey> needLoadSegments = new HashSet<SegmentKey>();
        SegmentLocationInfo.UpdateType updateType;
        SourceKey needLoadSourceKey;
        String sourceClusterId = event.getSourceClusterId();

        switch (event.subEvent()) {
            case LOAD_SEGMENT:
                updateType = SegmentLocationInfo.UpdateType.ALL;
                needLoadSourceKey = (SourceKey) event.getContent();
                if (null != needLoadSourceKey) {
                    List<SegmentKey> keys = allSegments.get(needLoadSourceKey);
                    needLoadSegments.addAll(keys);
                } else {
                    for (Map.Entry<SourceKey, List<SegmentKey>> segEntry : allSegments.entrySet()) {
                        needLoadSegments.addAll(segEntry.getValue());
                    }
                }
                break;
            case TRANS_COLLATE_LOAD:
                updateType = SegmentLocationInfo.UpdateType.PART;
                Pair<SourceKey, List<SegmentKey>> content = (Pair<SourceKey, List<SegmentKey>>) event.getContent();
                needLoadSourceKey = content.getKey();
                List<SegmentKey> contentSegmentKeys = content.getValue();
                if (null != needLoadSourceKey) {
                    List<SegmentKey> allSegmentKeys = allSegments.get(needLoadSourceKey);
                    List<SegmentKey> target = new ArrayList<SegmentKey>();
                    for (SegmentKey segmentKey : allSegmentKeys) {
                        if (contentSegmentKeys.contains(segmentKey)) {
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
            UploadService service = ProxySelector.getInstance().getFactory().getProxy(UploadService.class);
            service.download(needLoadSegments, updateType == SegmentLocationInfo.UpdateType.ALL);
            eventResult.setSuccess(true);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
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
