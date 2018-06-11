package com.fr.swift.service.handler.history;

import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.history.rule.DataSyncRule;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager implements Handler<HistoryLoadRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HistoryDataSyncManager.class);
    @Autowired
    private SwiftClusterSegmentService clusterSegmentService;
    private DataSyncRule rule = DataSyncRule.DEFAULT;

    public void setRule(DataSyncRule rule) {
        this.rule = rule;
    }

    public <S extends Serializable> S handle(HistoryLoadRpcEvent event) {
        // TODO 获取historyService代理
        Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
        List<SegmentKey> needLoadSegment = event.getContent();
        Map<String, List<SegmentKey>> keys = clusterSegmentService.getClusterSegments();
        Iterator<String> keyIterator = historyServiceMap.keySet().iterator();
        Map<String, List<SegmentKey>> exists = new HashMap<String, List<SegmentKey>>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            exists.put(key, keys.get(key));
        }
        Map<String, Set<URI>> result = rule.calculate(keys, exists, needLoadSegment);
        keyIterator = result.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            try {
                historyServiceMap.get(key).load(result.get(key));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
