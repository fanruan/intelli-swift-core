package com.fr.swift.segment.event;

import com.fr.swift.SwiftContext;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.exception.DefaultRepoNotFoundException;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.BaseSegment;
import com.fr.swift.segment.SegmentKey;
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
public class MaskHistoryListener implements SwiftEventListener<SegmentKey> {


    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    @Override
    public void on(final SegmentKey segKey) {
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
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            int currentDir = CubeUtil.getCurrentDir(segKey.getTable());
            String absoluteSegPath = new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).build();
            String local = String.format("%s/%s", absoluteSegPath, BaseSegment.ALL_SHOW_INDEX);
            String remote = String.format("%s/%s", new CubePathBuilder(segKey).build(), BaseSegment.ALL_SHOW_INDEX);
            try {
                SwiftRepositoryManager.getManager().currentRepo().zipToRemote(local, remote);
            } catch (DefaultRepoNotFoundException e) {
                SwiftLoggers.getLogger().warn("default repository not fount. ", e);
            } catch (IOException e) {
                SwiftLoggers.getLogger().error("mask segment {} failed", segKey, e);
            }
        }
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.MASK_HISTORY, new MaskHistoryListener());
    }

    public static void listen() {
    }
}