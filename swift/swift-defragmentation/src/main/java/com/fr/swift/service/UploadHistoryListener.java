package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.TransCollateLoadEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.util.Util;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#UPLOAD_HISTORY
 */
public class UploadHistoryListener implements SwiftEventListener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private static final SwiftSegmentLocationService LOCATION_SVC = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    @Override
    public void on(final SegmentKey segKey) {
        upload(segKey);
    }

    private static void upload(final SegmentKey segKey) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            int currentDir = CubeUtil.getCurrentDir(segKey.getTable());
            String local = new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).build();
            String remote = new CubePathBuilder(segKey).build();
            try {
                REPO.currentRepo().copyToRemote(local, remote);

                notifyDownload(segKey);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Cannot upload Segment which path is {}", local, e);
            }
        } else {
            SegmentKey realtimeSegKey = getRealtimeSegKey(segKey);
            SEG_SVC.removeSegments(Collections.singletonList(realtimeSegKey));
            SegmentUtils.clearSegment(realtimeSegKey);
        }
    }

    private static void notifyDownload(final SegmentKey segKey) {
        final String currentClusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        EventResult result = (EventResult) ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(
                new TransCollateLoadEvent(Pair.of(segKey.getTable(), Collections.singletonList(segKey)), currentClusterId));
        if (result.isSuccess()) {
            String clusterId = result.getClusterId();
            SegmentKey realtimeSegKey = getRealtimeSegKey(segKey);
            SEG_SVC.removeSegments(Collections.singletonList(realtimeSegKey));
            SegmentUtils.clearSegment(realtimeSegKey);

            // 删除本机上的history分布
            if (!Util.equals(clusterId, currentClusterId)) {
                LOCATION_SVC.delete(segKey.getTable().getId(), currentClusterId, segKey.toString());
                SegmentUtils.clearSegment(segKey);
            }
        } else {
            SwiftLoggers.getLogger().error(result.getError());
        }
    }

    private static SegmentKey getRealtimeSegKey(SegmentKey hisSegKey) {
        return new SegmentKeyBean(hisSegKey.getTable(), hisSegKey.getOrder(), StoreType.MEMORY, hisSegKey.getSwiftSchema());
    }

    private static final UploadHistoryListener INSTANCE = new UploadHistoryListener();

    public static void listen() {
        // todo 何时listen
        SwiftEventDispatcher.listen(SegmentEvent.UPLOAD_HISTORY, INSTANCE);
    }
}