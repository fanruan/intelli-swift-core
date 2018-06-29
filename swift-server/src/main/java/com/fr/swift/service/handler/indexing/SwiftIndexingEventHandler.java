package com.fr.swift.service.handler.indexing;

import com.fr.swift.cube.queue.StuffProviderQueue;
import com.fr.swift.cube.queue.SwiftImportStuff;
import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.service.handler.indexing.rule.IndexingSelectRule;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler extends AbstractHandler<AbstractIndexingRpcEvent> {

    private IndexingSelectRule rule = IndexingSelectRule.DEFAULT;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftIndexingEventHandler.class);

    public void setRule(IndexingSelectRule rule) {
        this.rule = rule;
    }

    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        Map<String, ClusterEntity> indexingServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
        if (null == indexingServices || indexingServices.isEmpty()) {
            throw new RuntimeException("Cannot find any Indexing Service!");
        }
        switch (event.subEvent()) {
            case INDEX:
                try {
                    IndexingStuff stuff = (IndexingStuff) event.getContent();
                    List<DataSource> dataSources = new ArrayList<DataSource>(stuff.getTables().values());
                    StuffProviderQueue.getQueue().put(new SwiftImportStuff(dataSources));
                } catch (Exception e) {
                    LOGGER.error("Indexing error! ", e);
                }
        }
        return null;
    }
}
