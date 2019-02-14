package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-01-09
 */
public class MaskHistoryListenerTest extends BaseHistoryListenerTest {

    @Test
    public void on() throws IOException, InterruptedException {
        init();
        MaskHistoryListener.listen();
        SwiftEventDispatcher.fire(SegmentEvent.MASK_HISTORY);
        PowerMock.verifyAll();
    }

}