package com.fr.swift.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.service.ScheduledRealtimeTransfer.RealtimeToHistoryTransfer;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#TRANSFER_REALTIME
 */
public class TransferRealtimeListener implements SwiftEventListener<SegmentKey> {

    private static final ServiceTaskExecutor TASK_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    @Override
    public void on(final SegmentKey segKey) {
        try {
            TASK_EXEC.submit(new SwiftServiceCallable(segKey.getTable(), ServiceTaskType.PERSIST) {
                @Override
                public void doJob() {
                    new RealtimeToHistoryTransfer(segKey).transfer();
                }
            });
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().warn("{} transfer to realtime failed: {}", segKey, e);
        }
    }

    private static final TransferRealtimeListener INSTANCE = new TransferRealtimeListener();

    public static void listen() {
        // todo 何时listen
        SwiftEventDispatcher.listen(SegmentEvent.TRANSFER_REALTIME, INSTANCE);
    }
}