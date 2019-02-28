package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.UploadExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ClusterSelector;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#MASK_HISTORY
 */
public class MaskHistoryListener implements SwiftEventListener<SegmentKey> {

    @Override
    public void on(final SegmentKey segKey) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            mask(segKey);
        }
    }

    private static void mask(final SegmentKey segKey) {
        try {
            TaskProducer.produceTask(UploadExecutorTask.ofAllShowIndex(segKey));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("persist task(upload {}'s all_show_index) failed", segKey, e);
        }
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.MASK_HISTORY, new MaskHistoryListener());
    }

    public static void listen() {
    }
}