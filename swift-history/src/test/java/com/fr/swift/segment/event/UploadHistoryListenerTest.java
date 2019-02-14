package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-01-09
 */
public class UploadHistoryListenerTest extends BaseHistoryListenerTest {

    @Test
    public void on() throws IOException, InterruptedException {
        init();
        RemoveHistoryListener.listen();
        SwiftEventDispatcher.fire(SegmentEvent.UPLOAD_HISTORY);
        PowerMock.verifyAll();
    }
}