package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-01-09
 */
public class RemoveHistoryListenerTest extends BaseHistoryListenerTest {

    @Test
    public void on() throws IOException, InterruptedException {
        init();
        RemoveHistoryListener.listen();
        SwiftEventDispatcher.fire(SegmentEvent.REMOVE_HISTORY);
        PowerMock.verifyAll();
    }
}