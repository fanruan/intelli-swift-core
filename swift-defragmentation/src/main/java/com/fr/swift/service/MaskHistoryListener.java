package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
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

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#MASK_HISTORY
 */
public class MaskHistoryListener implements SwiftEventListener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    @Override
    public void on(final SegmentKey segKey) {
        try {
            SVC_EXEC.submit(new SwiftServiceCallable(segKey.getTable(), ServiceTaskType.UPLOAD) {
                @Override
                public void doJob() {
                    mask(segKey);
                }
            });
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
        SwiftEventDispatcher.listen(SegmentEvent.MASK_HISTORY, INSTANCE);
    }
}