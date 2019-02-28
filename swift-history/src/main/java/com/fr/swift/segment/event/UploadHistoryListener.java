package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.history.TransCollateLoadEvent;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.UploadExecutorTask;
import com.fr.swift.executor.task.job.Job.JobListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#UPLOAD_HISTORY
 */
public class UploadHistoryListener implements SwiftEventListener<SegmentKey> {


    @Override
    public void on(final SegmentKey segKey) {
        if (SwiftProperty.getProperty().isCluster()) {
            upload(segKey);
        }
    }

    private static void upload(final SegmentKey segKey) {
        try {
            TaskProducer.produceTask(UploadExecutorTask.ofWholeSeg(segKey, new JobListener() {
                @Override
                public void onDone(boolean success) {
                    if (success) {
                        notifyDownload(segKey);
                    }
                }
            }));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("persist task(upload whore seg {}) failed", "persist task(upload {}'s all_show_index) failed", segKey, e);
        }
    }

    private static void notifyDownload(final SegmentKey segKey) {
        final String currentClusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(
                new TransCollateLoadEvent(Pair.of(segKey.getTable(), Collections.singletonList(segKey)), currentClusterId));
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.UPLOAD_HISTORY, new UploadHistoryListener());
    }

    public static void listen() {
    }
}