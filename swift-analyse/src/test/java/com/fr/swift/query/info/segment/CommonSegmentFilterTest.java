package com.fr.swift.query.info.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Create by lifan on 2019-07-30 13:33
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, CommonSegmentFilter.class})
public class CommonSegmentFilterTest {
    private static final SwiftSegmentManager swiftSegmentManager = mock(SwiftSegmentManager.class);

    private static final SwiftTableAllotRuleService ALLOT_RULE_SERVICE = mock(SwiftTableAllotRuleService.class);

    private static final SwiftSegmentBucketService SEGMENT_BUCKET_SERVICE = mock(SwiftSegmentBucketService.class);

    private static final SwiftSegmentBucket swiftSegmentBucket = mock(SwiftSegmentBucket.class);

    private static final SwiftTableAllotRule tableAllotRule = mock(SwiftTableAllotRule.class);

    private static final Map<Integer, List<SegmentKey>> bucketMap = mock(Map.class);

    @Before
    public void setUp() {
        //mock SwiftContext
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        //mock SwiftSegmentManager
        when(beanFactory.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(swiftSegmentManager);

        //mock ALLOT_RULE_SERVICE
        when(beanFactory.getBean(SwiftTableAllotRuleService.class)).thenReturn(ALLOT_RULE_SERVICE);

        //mock SEGMENT_BUCKET_SERVICE
        when(beanFactory.getBean(SwiftSegmentBucketService.class)).thenReturn(SEGMENT_BUCKET_SERVICE);

        //mock BucketMap
        when(swiftSegmentBucket.getBucketMap()).thenReturn(bucketMap);

    }

    @Test
    public void filterSegment() {

        Set<Integer> virtualOrders = new HashSet<Integer>();
        virtualOrders.add(1);

        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        SegmentKey segmentKey = PowerMockito.mock(SegmentKey.class);
        segmentKeyList.add(segmentKey);
        PowerMockito.when(bucketMap.get(1)).thenReturn(segmentKeyList);

        List<Segment> segmentList = new ArrayList<Segment>();
        Segment segment = PowerMockito.mock(Segment.class);
        PowerMockito.when(swiftSegmentManager.getSegment(segmentKey)).thenReturn(segment);
        segmentList.add(segment);

        Assert.assertEquals(segmentList, new CommonSegmentFilter(tableAllotRule, swiftSegmentBucket).filterSegment(virtualOrders));
    }
}