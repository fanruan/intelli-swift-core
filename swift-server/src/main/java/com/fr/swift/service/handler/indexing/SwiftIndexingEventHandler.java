package com.fr.swift.service.handler.indexing;

import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.event.indexing.IndexRpcEvent;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler implements Handler<AbstractIndexingRpcEvent> {
    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        // TODO 获取所有Indexing 节点
        List<IndexingService> indexingServices = new ArrayList<IndexingService>();
        switch (event.subEvent()) {
            case INDEX:
                // TODO 根据一定逻辑选择indexing节点
                indexingServices.get(0).index(((IndexRpcEvent) event).getContent());
        }
        return null;
    }
}
