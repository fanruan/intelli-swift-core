package com.fr.swift.event.anlayse;

import com.fr.swift.event.analyse.RequestSegLocationEvent;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class AnalyseEventTest extends TestCase {

    public void testRequestSegLocationEvent() {
        RequestSegLocationEvent event = new RequestSegLocationEvent("127.0.0.1:8080");
        assertEquals(event.getContent(), "127.0.0.1:8080");
        assertEquals(event.subEvent(), AbstractAnalyseRpcEvent.Event.REQUEST_SEG_LOCATION);
        assertEquals(event.type(), SwiftRpcEvent.EventType.ANALYSE);
    }

    public void testSegmentLocationRpcEvent() {
        SegmentLocationInfo segmentLocationInfo = EasyMock.createMock(SegmentLocationInfo.class);
        EasyMock.replay(segmentLocationInfo);
        SegmentLocationRpcEvent event = new SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType.ALL, segmentLocationInfo);
        assertEquals(event.getContent().getValue(), segmentLocationInfo);
        assertEquals(event.getContent().getKey(), SegmentLocationInfo.UpdateType.ALL);
        assertEquals(event.subEvent(), AbstractAnalyseRpcEvent.Event.SEGMENT_LOCATION);
        assertEquals(event.type(), SwiftRpcEvent.EventType.ANALYSE);
    }
}
