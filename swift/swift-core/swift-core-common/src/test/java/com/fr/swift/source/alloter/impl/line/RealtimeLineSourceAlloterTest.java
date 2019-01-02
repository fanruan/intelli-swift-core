package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2018/12/26.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, BeanFactory.class})
public class RealtimeLineSourceAlloterTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = PowerMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        PowerMock.replay(SwiftContext.class);
    }

    public void testAllot() {
        int newSegOrder = 7;
        SegmentKey newSeg = new SegmentKeyBean(new SourceKey(""), newSegOrder, Types.StoreType.MEMORY, null);
        Map<SourceKey, List<SegmentKey>> keyListMap = new HashMap<SourceKey, List<SegmentKey>>();
        List<SegmentKey> keys = new ArrayList<SegmentKey>();
        keys.add(new SegmentKeyBean(new SourceKey(""), 1, Types.StoreType.MEMORY, SwiftDatabase.CUBE));
        keys.add(new SegmentKeyBean(new SourceKey(""), 2, Types.StoreType.MEMORY, SwiftDatabase.CUBE));
        keyListMap.put(new SourceKey(""), keys);

        Segment seg1 = PowerMock.createMock(Segment.class);
        EasyMock.expect(seg1.isReadable()).andReturn(true).anyTimes();
        EasyMock.expect(seg1.getRowCount()).andReturn(998).times(1);
        Segment seg2 = PowerMock.createMock(Segment.class);
        EasyMock.expect(seg2.isReadable()).andReturn(true).anyTimes();
        EasyMock.expect(seg2.getRowCount()).andReturn(999).times(1);

        EasyMock.expect(seg1.getRowCount()).andReturn(998).times(1);
        EasyMock.expect(seg2.getRowCount()).andReturn(1000).times(1);
        PowerMock.replay(seg1, seg2);

        SwiftMetaDataService metaDataService = PowerMock.createMock(SwiftMetaDataService.class);
        SwiftMetaData metaData = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(metaDataService.getMetaDataByKey("")).andReturn(metaData).anyTimes();
        EasyMock.expect(SwiftContext.get().getBean(SwiftMetaDataService.class)).andReturn(metaDataService).anyTimes();

        SwiftCubePathService pathService = PowerMock.createMock(SwiftCubePathService.class);
        EasyMock.expect(pathService.getSwiftPath()).andReturn("").anyTimes();
        EasyMock.expect(SwiftContext.get().getBean(SwiftCubePathService.class)).andReturn(pathService).anyTimes();

        SwiftSegmentService segmentService = PowerMock.createMock(SwiftSegmentService.class);
        EasyMock.expect(segmentService.getOwnSegments()).andReturn(keyListMap).times(2);
        EasyMock.expect(segmentService.getOwnSegments()).andReturn(Collections.EMPTY_MAP).times(1);
        EasyMock.expect(segmentService.tryAppendSegment(new SourceKey(""), Types.StoreType.MEMORY))
                .andReturn(newSeg)
                .anyTimes();

        EasyMock.expect(SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class))
                .andReturn(segmentService).anyTimes();
        EasyMock.expect(SwiftContext.get().getBean(EasyMock.eq("realtimeSegment"), EasyMock.eq(Segment.class),
                EasyMock.notNull(IResourceLocation.class), EasyMock.eq(metaData)))
                .andReturn(seg1)
                .times(1);
        EasyMock.expect(SwiftContext.get().getBean(EasyMock.eq("realtimeSegment"), EasyMock.eq(Segment.class),
                EasyMock.notNull(IResourceLocation.class), EasyMock.eq(metaData)))
                .andReturn(seg2)
                .times(1);
        EasyMock.expect(SwiftContext.get().getBean(EasyMock.eq("realtimeSegment"), EasyMock.eq(Segment.class),
                EasyMock.notNull(IResourceLocation.class), EasyMock.eq(metaData)))
                .andReturn(seg1)
                .times(1);
        EasyMock.expect(SwiftContext.get().getBean(EasyMock.eq("realtimeSegment"), EasyMock.eq(Segment.class),
                EasyMock.notNull(IResourceLocation.class), EasyMock.eq(metaData)))
                .andReturn(seg2)
                .times(1);
        PowerMock.replay(SwiftContext.get(), segmentService, metaDataService, pathService, metaData);
        SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(new SourceKey(""), new LineAllotRule(1000));
        // 插入一行
        SegmentInfo segmentInfo = alloter.allot(new LineRowInfo(0));
        assertEquals(2, segmentInfo.getOrder());
        assertEquals(Types.StoreType.MEMORY, segmentInfo.getStoreType());
        // 再插入一行（此时seg2已经满了），所以插入seg1
        segmentInfo = alloter.allot(new LineRowInfo(0));
        assertEquals(1, segmentInfo.getOrder());
        assertEquals(Types.StoreType.MEMORY, segmentInfo.getStoreType());
        // seg1再插入一行
        segmentInfo = alloter.allot(new LineRowInfo(0));
        assertEquals(1, segmentInfo.getOrder());
        assertEquals(Types.StoreType.MEMORY, segmentInfo.getStoreType());
        // seg1也满了，创建新块
        segmentInfo = alloter.allot(new LineRowInfo(0));
        assertEquals(newSegOrder, segmentInfo.getOrder());
        assertEquals(Types.StoreType.MEMORY, segmentInfo.getStoreType());
    }
}