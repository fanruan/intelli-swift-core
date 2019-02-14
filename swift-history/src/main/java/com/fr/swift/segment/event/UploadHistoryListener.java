package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.history.TransCollateLoadEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.structure.Pair;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#UPLOAD_HISTORY
 */
public class UploadHistoryListener implements SwiftEventListener<SegmentKey> {


    @Override
    public void on(final SegmentKey segKey) {
        upload(segKey);
    }

    private static void upload(final SegmentKey segKey) {
        if (SwiftProperty.getProperty().isCluster()) {
            int currentDir = CubeUtil.getCurrentDir(segKey.getTable());
            String local = new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).build();
            String remote = new CubePathBuilder(segKey).build();
            try {
                SwiftRepositoryManager.getManager().currentRepo().copyToRemote(local, remote);

                notifyDownload(segKey);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Cannot upload Segment which path is {}", local, e);
            }
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