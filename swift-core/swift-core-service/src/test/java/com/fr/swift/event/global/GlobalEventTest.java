package com.fr.swift.event.global;

import com.fr.swift.db.Where;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class GlobalEventTest extends TestCase {
    public void testCheckMasterEvent() {
        CheckMasterEvent event = new CheckMasterEvent();
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.CHECK_MASTER);
        assertNull(event.getContent());
    }

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
        GetAnalyseAndRealTimeAddrEvent event = new GetAnalyseAndRealTimeAddrEvent();
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.GET_ANALYSE_REAL_TIME);
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

    public void testTaskDoneRpcEvent() {
        Pair<TaskKey, TaskResult> pair = EasyMock.createMock(Pair.class);
        EasyMock.replay(pair);
        TaskDoneRpcEvent event = new TaskDoneRpcEvent(pair);
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.TASK_DONE);
        assertEquals(event.getContent(), pair);
    }

    public void testTruncateEvent() {

        TruncateEvent event = new TruncateEvent("tableA");
        assertEquals(event.type(), SwiftRpcEvent.EventType.GLOBAL);
        assertEquals(event.subEvent(), AbstractGlobalRpcEvent.Event.TRUNCATE);
        assertEquals(event.getContent(), new SourceKey("tableA"));
    }
}
