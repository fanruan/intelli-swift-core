package com.fr.swift.segment.collate;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class FragmentCollectRuleTest extends TestCase {
    SwiftSegmentManager localSegments;
    SegmentKey realtimeSegkey;
    SegmentKey historySegkey;
    SwiftSourceAlloter swiftSourceAlloter;
    Segment realSegment;
    Segment hisSegment;

    @Override
    public void setUp() throws Exception {
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        localSegments = EasyMock.createMock(SwiftSegmentManager.class);
        EasyMock.expect(beanFactory.getBean("localSegmentProvider", SwiftSegmentManager.class)).andReturn(localSegments).anyTimes();
        realtimeSegkey = EasyMock.createMock(SegmentKey.class);
        historySegkey = EasyMock.createMock(SegmentKey.class);
        realSegment = EasyMock.createMock(Segment.class);
        hisSegment = EasyMock.createMock(Segment.class);
        EasyMock.expect(hisSegment.getRowCount()).andReturn(100).anyTimes();
        EasyMock.expect(hisSegment.getAllShowIndex()).andReturn(BitMaps.newAllShowBitMap(100)).anyTimes();


        EasyMock.expect(localSegments.getSegment(realtimeSegkey)).andReturn(realSegment).anyTimes();
        EasyMock.expect(localSegments.getSegment(historySegkey)).andReturn(hisSegment).anyTimes();

        IResourceLocation realLocation = EasyMock.createMock(IResourceLocation.class);
        IResourceLocation hisLocation = EasyMock.createMock(IResourceLocation.class);

        EasyMock.expect(realSegment.getLocation()).andReturn(realLocation).anyTimes();
        EasyMock.expect(hisSegment.getLocation()).andReturn(hisLocation).anyTimes();
        EasyMock.expect(realLocation.getStoreType()).andReturn(Types.StoreType.MEMORY).anyTimes();
        EasyMock.expect(hisLocation.getStoreType()).andReturn(Types.StoreType.FINE_IO).anyTimes();


        swiftSourceAlloter = EasyMock.createMock(SwiftSourceAlloter.class);
        AllotRule allotRule = EasyMock.createMock(AllotRule.class);
        EasyMock.expect(swiftSourceAlloter.getAllotRule()).andReturn(allotRule).anyTimes();
        EasyMock.expect(allotRule.getCapacity()).andReturn(100).anyTimes();
        PowerMock.replay(SwiftContext.class);

        EasyMock.replay(beanFactory, localSegments, realLocation, realSegment, realtimeSegkey, hisLocation, hisSegment, historySegkey, swiftSourceAlloter, allotRule);

    }

    public void testSwiftFragmentCollectRule() {

        SwiftFragmentCollectRule rule = new SwiftFragmentCollectRule(swiftSourceAlloter);
        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        assertTrue(rule.collect(Collections.singletonList(realtimeSegkey)).isEmpty());
        for (int i = 0; i < 20; i++) {
            segmentKeyList.add(realtimeSegkey);
        }
        assertEquals(rule.collect(segmentKeyList).size(), 20);

        segmentKeyList.clear();
        for (int i = 0; i < 20; i++) {
            segmentKeyList.add(historySegkey);
        }
        assertTrue(rule.collect(segmentKeyList).isEmpty());

    }
}
