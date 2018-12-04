package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.BaseSegment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#MASK_HISTORY
 */
public class MaskHistoryListener extends Listener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    @Override
    public void on(Event event, final SegmentKey segKey) {
        try {
            SVC_EXEC.submit(new SwiftServiceCallable<Void>(segKey.getTable(), ServiceTaskType.UPLOAD, new Callable<Void>() {
                @Override
                public Void call() {
                    mask(segKey);
                    return null;
                }
            }));
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static void mask(final SegmentKey segKey) {
        FineIO.doWhenFinished(new Runnable() {
            @Override
            public void run() {
                if (ClusterSelector.getInstance().getFactory().isCluster()) {
                    String local = String.format("%s/%s", CubeUtil.getAbsoluteSegPath(segKey), BaseSegment.ALL_SHOW_INDEX);
                    String remote = String.format("%s/%s/%s", segKey.getSwiftSchema().getDir(), segKey.getUri().getPath(), BaseSegment.ALL_SHOW_INDEX);
                    try {
                        REPO.currentRepo().zipToRemote(local, remote);
                    } catch (IOException e) {
                        SwiftLoggers.getLogger().error("mask segment {} failed", segKey, e);
                    }
                }
            }
        });
    }

    public static final MaskHistoryListener INSTANCE = new MaskHistoryListener();

    public static void listen() {
        EventDispatcher.listen(SegmentEvent.MASK_HISTORY, INSTANCE);
    }
}