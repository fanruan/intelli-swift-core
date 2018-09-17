package com.fr.swift.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;

import java.io.IOException;

/**
 * @author anchore
 * @date 2018/9/11
 */
public class UnloadHistoryListener extends Listener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    @Override
    public void on(Event event, SegmentKey segKey) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            String remote = String.format("%s/%s", segKey.getSwiftSchema().getDir(), segKey.getUri().getPath());
            try {
                REPO.currentRepo().delete(remote);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error("unload segment {} failed", segKey, e);
            }
        }
    }

    public static final UnloadHistoryListener INSTANCE = new UnloadHistoryListener();

    public static void listen() {
        EventDispatcher.listen(SegmentEvent.UNLOAD_HISTORY, INSTANCE);
    }
}