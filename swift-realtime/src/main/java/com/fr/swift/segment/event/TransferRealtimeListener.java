package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.TransferExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.event.TransferRealtimeListener.TransferRealtimeEventData;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#TRANSFER_REALTIME
 */
public class TransferRealtimeListener implements SwiftEventListener<TransferRealtimeEventData> {

    @Override
    public void on(TransferRealtimeEventData eventData) {
        try {
            // TODO: 2019/3/5 考虑看看是否提到上层
            TaskProducer.produceTask(eventData.isPassive() ?
                    TransferExecutorTask.ofPassive(eventData.getSegKey()) :
                    TransferExecutorTask.ofActive(eventData.getSegKey()));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("persist task(transfer realtime seg {}) failed", eventData.getSegKey(), e);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    static {
        SwiftEventDispatcher.listen(SegmentEvent.TRANSFER_REALTIME, new TransferRealtimeListener());
    }

    public static void listen() {
    }

    public static class TransferRealtimeEventData {
        private SegmentKey segKey;
        private boolean passive;

        private TransferRealtimeEventData(SegmentKey segKey, boolean passive) {
            this.segKey = segKey;
            this.passive = passive;
        }

        public static TransferRealtimeEventData ofPassive(SegmentKey segKey) {
            return new TransferRealtimeEventData(segKey, true);
        }

        public static TransferRealtimeEventData ofActive(SegmentKey segKey) {
            return new TransferRealtimeEventData(segKey, false);
        }

        public SegmentKey getSegKey() {
            return segKey;
        }

        public boolean isPassive() {
            return passive;
        }
    }
}