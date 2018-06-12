package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.service.IndexingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    IndexingSelectRule DEFAULT = new IndexingSelectRule() {
        /**
         * 获取所有索引节点当前运行状态
         * 状况最佳的去索引
         *
         * @param indexingServices
         * @return
         */
        @Override
        public IndexingService select(Map<String, IndexingService> indexingServices) {
            Iterator<Map.Entry<String, IndexingService>> iterator = indexingServices.entrySet().iterator();
            List<ServerCurrentStatus> serverCurrentStatuses = new ArrayList<ServerCurrentStatus>();
            while (iterator.hasNext()) {
                serverCurrentStatuses.add(iterator.next().getValue().currentStatus());
            }
            Collections.sort(serverCurrentStatuses);
            return indexingServices.get(serverCurrentStatuses.get(0).getClusterId());
        }
    };

    IndexingService select(Map<String, IndexingService> indexingServices);

}
