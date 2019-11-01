package com.fr.swift.event.global;

import com.fr.swift.db.Where;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class GlobalEventTest extends TestCase {

    public void testCleanMetaDataCacheEvent() {
        CleanMetaDataCacheEvent event = new CleanMetaDataCacheEvent(new SourceKey[0]);
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.CLEAN);
        assertEquals(event.getContent().length, 0);
    }

    public void testDeleteEvent() {
        Pair<SourceKey, Where> pair = EasyMock.createMock(Pair.class);
        EasyMock.replay(pair);
        DeleteEvent event = new DeleteEvent(pair);
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.DELETE);
        assertEquals(event.getContent(), pair);
    }

    public void testGetAnalyseAndRealTimeAddrEvent() {
        GetJdbcAddresses event = new GetJdbcAddresses();
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.GET_JDBC_ADDRESS);
        assertNull(event.getContent());
    }

    public void testPushSegLocationRpcEvent() {
        SegmentLocationInfo segmentLocationInfo = EasyMock.createMock(SegmentLocationInfo.class);
        EasyMock.replay(segmentLocationInfo);
        PushSegLocationRpcEvent event = new PushSegLocationRpcEvent(segmentLocationInfo);
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.PUSH_SEG);
        assertEquals(event.getContent(), segmentLocationInfo);
    }

    public void testRemoveSegLocationRpcEvent() {
        SegmentLocationInfo segmentLocationInfo = EasyMock.createMock(SegmentLocationInfo.class);
        EasyMock.replay(segmentLocationInfo);
        RemoveSegLocationRpcEvent event = new RemoveSegLocationRpcEvent("127.0.0.1:8080", segmentLocationInfo);
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.REMOVE_SEG);
        assertEquals(event.getContent(), segmentLocationInfo);
        assertEquals(event.getClusterId(), "127.0.0.1:8080");
    }

    public void testTruncateEvent() {

        TruncateEvent event = new TruncateEvent("tableA");
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.TRUNCATE);
        assertEquals(event.getContent(), new SourceKey("tableA"));
    }
}
