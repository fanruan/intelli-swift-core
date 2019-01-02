package com.fr.swift.segment.event;

import com.fr.swift.SwiftContext;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ScheduledRealtimeTransfer.RealtimeToHistoryTransfer;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.util.concurrent.Callable;

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
            TASK_EXEC.submit(new SwiftServiceCallable<Void>(segKey.getTable(), ServiceTaskType.PERSIST, new Callable<Void>() {
                @Override
                public Void call() {
                    new RealtimeToHistoryTransfer(segKey).transfer();
                    return null;
                }
            }));
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().warn("{} transfer to realtime failed: {}", segKey, e);
        }
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.TRANSFER_REALTIME, new TransferRealtimeListener());
    }

    public static void listen() {
    }
}