package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.info.ServerInfo;
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
            List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
            while (iterator.hasNext()) {
                serverInfos.add(iterator.next().getValue().serverInfo());
            }
            Collections.sort(serverInfos);
            return indexingServices.get(serverInfos.get(0).getClusterId());
        }
    };

    IndexingService select(Map<String, IndexingService> indexingServices);

}
