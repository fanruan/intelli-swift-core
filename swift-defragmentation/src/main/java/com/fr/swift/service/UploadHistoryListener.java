package com.fr.swift.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.TransCollateLoadEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.utils.ClusterCommonUtils;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 */
public class UploadHistoryListener extends Listener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    @Override
    public void on(Event event, final SegmentKey segKey) {
        try {
            SVC_EXEC.submit(new SwiftServiceCallable(segKey.getTable(), ServiceTaskType.UPLOAD) {
                @Override
                public void doJob() {
                    upload(segKey);
                }
            });
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static void upload(SegmentKey segKey) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            String local = CubeUtil.getAbsoluteSegPath(segKey);
            String remote = String.format("%s/%s", segKey.getSwiftSchema().getDir(), segKey.getUri().getPath());
            try {
                REPO.currentRepo().copyToRemote(local, remote);

                notifyDownload(segKey);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(String.format("Cannot upload Segment which path is %s", local), e);
            }
        }
    }

    private static void notifyDownload(final SegmentKey segKey) throws Exception {
        ClusterCommonUtils.asyncCallMaster(
                new TransCollateLoadEvent(Pair.of(segKey.getTable().getId(), Collections.singletonList(segKey.toString())))
        ).addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                if (result instanceof EventResult) {
                    if (result == EventResult.SUCCESS) {
                        // TODO 删配置等等
                        SegmentKey realtimeSegKey = getRealtimeSegKey(segKey);
                        SEG_SVC.removeSegments(Collections.singletonList(realtimeSegKey));
                        SegmentUtils.clearSegment(realtimeSegKey);
                        SegmentUtils.clearSegment(segKey);
                    }
                }
            }

            @Override
            public void fail(Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        });
    }

    private static SegmentKey getRealtimeSegKey(SegmentKey hisSegKey) {
        return new SegmentKeyBean(hisSegKey.getTable(), hisSegKey.getOrder(), StoreType.MEMORY, hisSegKey.getSwiftSchema());
    }

    private static final UploadHistoryListener INSTANCE = new UploadHistoryListener();

    public static void listen() {
        // todo 何时listen
        EventDispatcher.listen(SegmentEvent.UPLOAD_HISTORY, INSTANCE);
    }
}