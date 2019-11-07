package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.TransferExecutorTask;
import com.fr.swift.log.SwiftLoggers;

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

}