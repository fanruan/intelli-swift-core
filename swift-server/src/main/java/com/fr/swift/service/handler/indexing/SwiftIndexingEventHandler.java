package com.fr.swift.service.handler.indexing;

import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.event.indexing.IndexRpcEvent;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.indexing.rule.IndexingSelectRule;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler implements Handler<AbstractIndexingRpcEvent> {

    private IndexingSelectRule rule = IndexingSelectRule.DEFAULT;

    public void setRule(IndexingSelectRule rule) {
        this.rule = rule;
    }

    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        // TODO 获取所有Indexing 节点
        Map<String, IndexingService> indexingServices = new HashMap<String, IndexingService>();
        switch (event.subEvent()) {
            case INDEX:
                rule.select(indexingServices).index(((IndexRpcEvent) event).getContent());
        }
        return null;
    }
}
