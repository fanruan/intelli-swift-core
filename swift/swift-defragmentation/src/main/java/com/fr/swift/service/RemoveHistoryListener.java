package com.fr.swift.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#REMOVE_HISTORY
 */
public class RemoveHistoryListener extends Listener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    @Override
    public void on(Event event, final SegmentKey segKey) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            String remote = String.format("%s/%s", segKey.getSwiftSchema().getDir(), segKey.getUri().getPath());
            try {
                REPO.currentRepo().delete(remote);

                SEG_SVC.removeSegments(Collections.singletonList(segKey));
                clearSeg(segKey);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error("unload segment {} failed", segKey, e);
            }
        } else {
            SEG_SVC.removeSegments(Collections.singletonList(segKey));
            clearSeg(segKey);
        }
    }

    private void clearSeg(final SegmentKey segKey) {
        try {
            SVC_EXEC.submit(new SwiftServiceCallable<Void>(segKey.getTable(), ServiceTaskType.CLEAR_LOCAL, new Callable<Void>() {
                @Override
                public Void call() {
                    SegmentUtils.clearSegment(segKey);
                    return null;
                }
            }));
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    public static final RemoveHistoryListener INSTANCE = new RemoveHistoryListener();

    public static void listen() {
        EventDispatcher.listen(SegmentEvent.REMOVE_HISTORY, INSTANCE);
    }
}