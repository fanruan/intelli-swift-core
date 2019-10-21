package com.fr.swift.segment.collate;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class FragmentClassifyTest {

    @Mock
    SegmentKey segmentKey;
    @Mock
    SegmentKey segmentKey0;

    SwiftSegmentBucket segmentBucket;

    @Before
    public void setUp() throws Exception {
        segmentBucket = new SwiftSegmentBucket(new SourceKey("test"));
        for (int i = 0; i < 10; i++) {
            segmentBucket.put(0, segmentKey0);
        }
    }

    @Test
    public void test() {
        SwiftFragmentClassify fragmentClassify = new SwiftFragmentClassify(segmentBucket);

        List<SegmentKey> list = new ArrayList<SegmentKey>();
        list.add(segmentKey);
        for (int i = 0; i < 10; i++) {
            list.add(segmentKey0);
        }

        Map<Integer, List<SegmentKey>> map = fragmentClassify.classify(list);
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(0).size(), 10);

    }
}
