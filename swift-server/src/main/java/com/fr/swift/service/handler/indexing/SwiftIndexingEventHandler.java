package com.fr.swift.service.handler.indexing;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cube.queue.StuffProviderQueue;
import com.fr.swift.cube.queue.SwiftImportStuff;
import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.IndexingStuff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/8
 */
@SwiftBean
public class SwiftIndexingEventHandler extends AbstractHandler<AbstractIndexingRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftIndexingEventHandler.class);

    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        switch (event.subEvent()) {
            case INDEX:
                try {
                    IndexingStuff stuff = (IndexingStuff) event.getContent();
                    List<DataSource> dataSources = new ArrayList<DataSource>(stuff.getTables().values());
                    StuffProviderQueue.getQueue().put(new SwiftImportStuff(dataSources));
                } catch (Exception e) {
                    LOGGER.error("Indexing error! ", e);
                }
                break;
            default:
                break;
        }
        return null;
    }
}
