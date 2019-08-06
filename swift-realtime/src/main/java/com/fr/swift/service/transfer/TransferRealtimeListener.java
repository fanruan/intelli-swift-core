package com.fr.swift.service.transfer;

import com.fr.swift.SwiftContext;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.SegmentEvent;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#TRANSFER_REALTIME
 */
public class TransferRealtimeListener implements SwiftEventListener<SegmentKey> {

    private static final com.fr.swift.segment.event.TransferRealtimeListener TASK_EXEC = SwiftContext.get().getBean(com.fr.swift.segment.event.TransferRealtimeListener.class);

    @Override
    public void on(final SegmentKey segKey) {
        TASK_EXEC.on(segKey);
    }

    private static final TransferRealtimeListener INSTANCE = new TransferRealtimeListener();

    public static void listen() {
        // todo 何时listen
        SwiftEventDispatcher.listen(SegmentEvent.TRANSFER_REALTIME, INSTANCE);
    }
}