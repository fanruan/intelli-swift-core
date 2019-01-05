package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class HistoryEventTest extends TestCase {

    public void testCheckLoadHistoryEvent() {
        CheckLoadHistoryEvent event = new CheckLoadHistoryEvent("127.0.0.1:8080");
        assertEquals(event.type(), SwiftRpcEvent.EventType.HISTORY);
        assertEquals(event.subEvent(), AbstractHistoryRpcEvent.Event.CHECK_LOAD);
        assertNull(event.getContent());
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8080");
    }

    public void testHistoryCommonLoadRpcEvent() {
        Pair<SourceKey, Map<SegmentKey, List<String>>> pair = EasyMock.createMock(Pair.class);
        EasyMock.replay(pair);
        HistoryCommonLoadRpcEvent event = new HistoryCommonLoadRpcEvent(pair, "127.0.0.1:8080");
        assertEquals(event.type(), SwiftRpcEvent.EventType.HISTORY);
        assertEquals(event.subEvent(), AbstractHistoryRpcEvent.Event.COMMON_LOAD);
        assertEquals(event.getContent(), pair);
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8080");
    }

    public void testHistoryLoadSegmentRpcEvent() {
        HistoryLoadSegmentRpcEvent event = new HistoryLoadSegmentRpcEvent(new SourceKey("tableA"), "127.0.0.1:8080");
        assertEquals(event.type(), SwiftRpcEvent.EventType.HISTORY);
        assertEquals(event.subEvent(), AbstractHistoryRpcEvent.Event.LOAD_SEGMENT);
        assertEquals(event.getContent(), new SourceKey("tableA"));
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8080");
        event.setSourceClusterId("127.0.0.1:8081");
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8081");
    }

    public void testModifyLoadRpcEvent() {
        Pair<SourceKey, Map<SegmentKey, List<String>>> pair = EasyMock.createMock(Pair.class);
        EasyMock.replay(pair);
        ModifyLoadRpcEvent event = new ModifyLoadRpcEvent(pair, "127.0.0.1:8080");
        assertEquals(event.type(), SwiftRpcEvent.EventType.HISTORY);
        assertEquals(event.subEvent(), AbstractHistoryRpcEvent.Event.MODIFY_LOAD);
        assertEquals(event.getContent(), pair);
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8080");
    }

    public void testTransCollateLoadEvent() {
        Pair<SourceKey, List<SegmentKey>> pair = EasyMock.createMock(Pair.class);
        EasyMock.replay(pair);
        TransCollateLoadEvent event = new TransCollateLoadEvent(pair, "127.0.0.1:8080");
        assertEquals(event.type(), SwiftRpcEvent.EventType.HISTORY);
        assertEquals(event.subEvent(), AbstractHistoryRpcEvent.Event.TRANS_COLLATE_LOAD);
        assertEquals(event.getContent(), pair);
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8080");
        event.setSourceClusterId("127.0.0.1:8081");
        assertEquals(event.getSourceClusterId(), "127.0.0.1:8081");
    }
}
