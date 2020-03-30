//package com.fr.swift.query.info.segment;
//
//import com.fr.swift.SwiftContext;
//import com.fr.swift.beans.factory.BeanFactory;
//import com.fr.swift.config.entity.SwiftSegmentBucket;
//import com.fr.swift.config.entity.SwiftTableAllotRule;
//import com.fr.swift.query.info.SingleTableQueryInfo;
//import com.fr.swift.segment.ReadonlyMultiSegment;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.SegmentKey;
//import com.fr.swift.segment.SwiftSegmentManager;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static org.powermock.api.mockito.PowerMockito.mock;
//import static org.powermock.api.mockito.PowerMockito.mockStatic;
//import static org.powermock.api.mockito.PowerMockito.when;
//import static org.powermock.api.mockito.PowerMockito.whenNew;
//
///**
// * Create by lifan on 2019-08-01 11:02
// */
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SwiftContext.class, CommonSegmentFilter.class, FunnelSegmentFilter.class})
//public class FunnelSegmentFilterTest {
//    private static final SwiftSegmentManager swiftSegmentManager = mock(SwiftSegmentManager.class);
//
//    private static final SwiftTableAllotRuleService ALLOT_RULE_SERVICE = mock(SwiftTableAllotRuleService.class);
//
//    private static final SwiftSegmentBucketService SEGMENT_BUCKET_SERVICE = mock(SwiftSegmentBucketService.class);
//
//    private static final SwiftSegmentBucket swiftSegmentBucket = mock(SwiftSegmentBucket.class);
//
//    private static final SwiftTableAllotRule tableAllotRule = mock(SwiftTableAllotRule.class);
//
//    private static final Map<Integer, List<SegmentKey>> bucketMap = mock(Map.class);
//
//    @Before
//    public void setUp() {
//        //mock SwiftContext
//        mockStatic(SwiftContext.class);
//        BeanFactory beanFactory = mock(BeanFactory.class);
//        when(SwiftContext.get()).thenReturn(beanFactory);
//
//        //mock SwiftSegmentManager
//        when(beanFactory.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(swiftSegmentManager);
//
//        //mock ALLOT_RULE_SERVICE
//        when(beanFactory.getBean(SwiftTableAllotRuleService.class)).thenReturn(ALLOT_RULE_SERVICE);
//
//        //mock SEGMENT_BUCKET_SERVICE
//        when(beanFactory.getBean(SwiftSegmentBucketService.class)).thenReturn(SEGMENT_BUCKET_SERVICE);
//
//        //mock BucketMap
//        when(swiftSegmentBucket.getBucketMap()).thenReturn(bucketMap);
//    }
//
//    @Test
//    public void filter() throws Exception {
//        SingleTableQueryInfo singleTableQueryInfo = mock(SingleTableQueryInfo.class);
//
//        Set<Integer> virtualOrders = new HashSet<Integer>();
//        virtualOrders.add(1);
//
//        SegmentKey segmentKey1 = mock(SegmentKey.class);
//        SegmentKey segmentKey2 = mock(SegmentKey.class);
//        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
//        segmentKeyList.add(segmentKey1);
//        segmentKeyList.add(segmentKey2);
//        when(bucketMap.get(1)).thenReturn(segmentKeyList);
//
//        Segment segment1 = mock(Segment.class);
//        Segment segment2 = mock(Segment.class);
//        when(swiftSegmentManager.getSegment(segmentKey1)).thenReturn(segment1);
//        when(swiftSegmentManager.getSegment(segmentKey2)).thenReturn(segment2);
//
//        List<Segment> segmentList = new ArrayList<Segment>();
//        segmentList.add(segment1);
//        segmentList.add(segment2);
//        ReadonlyMultiSegment readonlyMultiSegment = mock(ReadonlyMultiSegment.class);
//        whenNew(ReadonlyMultiSegment.class).withArguments(segmentList).thenReturn(readonlyMultiSegment);
//
//        List<Segment> funnelSegmentList = new ArrayList<Segment>();
//        funnelSegmentList.add(readonlyMultiSegment);
//
//        Assert.assertEquals(funnelSegmentList, new FunnelSegmentFilter(tableAllotRule, swiftSegmentBucket).filterSegment(virtualOrders, singleTableQueryInfo));
//
//    }
//
//
//}