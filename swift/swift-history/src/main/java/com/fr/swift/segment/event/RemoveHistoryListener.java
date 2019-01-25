package com.fr.swift.segment.event;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#REMOVE_HISTORY
 */
public class RemoveHistoryListener implements SwiftEventListener<SegmentKey> {


    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    @Override
    public void on(final SegmentKey segKey) {
        if (SwiftProperty.getProperty().isCluster()) {
            String remote = new CubePathBuilder(segKey).build();
            try {
                SwiftRepositoryManager.getManager().currentRepo().delete(remote);

                SEG_SVC.removeSegments(Collections.singletonList(segKey));
                SegmentUtils.clearSegment(segKey);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("unload segment {} failed", segKey, e);
            }
        } else {
            SEG_SVC.removeSegments(Collections.singletonList(segKey));
            SegmentUtils.clearSegment(segKey);
        }

        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.REMOVE_SEG, Collections.singletonList(segKey));
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.REMOVE_HISTORY, new RemoveHistoryListener());
    }

    public static void listen() {
    }
}