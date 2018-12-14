package com.fr.swift.segment.rule;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/7/17
 */
@RunWith(Parameterized.class)
public class DefaultSegmentDestSelectRuleTest {

    private List<SegmentDestination> selectDestination;

    public DefaultSegmentDestSelectRuleTest(Set<String> nodeIds, Set<SegmentKey> needLoad) {
        System.out.println("NodeSize： " + nodeIds.size() + " SegCount: " + needLoad.size());
        HashMap<SourceKey, List<SegmentDestination>> dest = new HashMap<SourceKey, List<SegmentDestination>>();
//        new DefaultDataSyncRule().getNeedLoadAndUpdateDestinations(nodeIds, needLoad, dest);
        selectDestination = dest.get("tableA");
    }

    @Parameterized.Parameters
    public static List<Object[]> randomParams() {
        List<Object[]> result = new ArrayList<Object[]>();
        Set<SegmentKey> needLoad = new HashSet<SegmentKey>();

//        needLoad.put("tableA", new ArrayList<SegmentKey>(100));
        for (int j = 0; j < 100; j++) {
            IMocksControl control = EasyMock.createControl();
            SegmentKey mockSegmentKey = control.createMock(SegmentKey.class);
            EasyMock.expect(mockSegmentKey.getOrder()).andReturn(j).anyTimes();
            EasyMock.expect(mockSegmentKey.getTable()).andReturn(new SourceKey("tableA")).anyTimes();
            EasyMock.expect(mockSegmentKey.getId()).andReturn("tableA@FINE_IO@" + j).anyTimes();
            control.replay();
            needLoad.add(mockSegmentKey);
        }
        for (int i = 0; i < 100; i++) {
            int nodeCount = (int) (1 + Math.random() * 100);
            Set<String> nodeIds = new HashSet<String>();
            for (int j = 0; j < nodeCount; j++) {
                nodeIds.add("cluster_" + j);
            }

            result.add(new Object[]{nodeIds, needLoad});
        }
        return result;
    }

    @Before
    public void setUp() throws Exception {
//        Preparer.prepareCubeBuild(getClass());
    }

    @Test
    public void selectDestination() {
        List<SegmentDestination> dest = new DefaultSegmentDestSelectRule().selectDestination(selectDestination);
        assertEquals(dest.size(), 100);
        for (int i = 0; i < dest.size() - 1; i++) {
            assertTrue(dest.get(i).getOrder() < dest.get(i + 1).getOrder());
            assertTrue(dest.get(i).getOrder() + 1 == dest.get(i + 1).getOrder());
        }
    }
}