package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.service.IndexingService;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    IndexingSelectRule DEFAULT = new IndexingSelectRule() {
        @Override
        public IndexingService select(List<IndexingService> indexingServices) {
            return null;
        }
    };

    IndexingService select(List<IndexingService> indexingServices);

}
