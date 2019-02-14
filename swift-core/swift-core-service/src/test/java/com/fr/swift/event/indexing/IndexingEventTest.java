package com.fr.swift.event.indexing;

import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.stuff.IndexingStuff;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class IndexingEventTest extends TestCase {

    public void testIndexRpcEvent() {
        IndexingStuff indexingStuff = EasyMock.createMock(IndexingStuff.class);
        EasyMock.replay(indexingStuff);
        IndexRpcEvent event = new IndexRpcEvent(indexingStuff);
        assertEquals(event.type(), SwiftRpcEvent.EventType.INDEXING);
        assertEquals(event.getContent(), indexingStuff);
        assertEquals(event.subEvent(), AbstractIndexingRpcEvent.Event.INDEX);
    }
}
