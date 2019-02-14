package com.fr.swift.service.handler.history.rule;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DefaultDataSyncRuleTest {
    private Set<String> nodeIds;
    private Set<SegmentKey> needLoad;

    public DefaultDataSyncRuleTest(Set<String> nodeIds, Set<SegmentKey> needLoad) {
        this.nodeIds = nodeIds;
        this.needLoad = needLoad;
    }

    @Before
    public void setUp() throws Exception {
    }

    @Parameterized.Parameters
    public static List<Object[]> randomParams() {
        Set<SegmentKey> needLoad = new HashSet<SegmentKey>();
        for (int j = 0; j < 100; j++) {
            IMocksControl control = EasyMock.createControl();
            SegmentKey mockSegmentKey = control.createMock(SegmentKey.class);
            EasyMock.expect(mockSegmentKey.getId()).andReturn("tableA@FINE_IO@" + j).anyTimes();
            EasyMock.expect(mockSegmentKey.getTable()).andReturn(new SourceKey("tableA")).anyTimes();
            EasyMock.expect(mockSegmentKey.getOrder()).andReturn(j).anyTimes();
            control.replay();
            needLoad.add(mockSegmentKey);
        }
        List<Object[]> result = new ArrayList<Object[]>();
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

    @Test
    public void calculate() {
        System.out.println("NodeSize： " + nodeIds.size() + " SegCount: " + needLoad.size());
        Map<String, Set<SegmentKey>> target = new DefaultDataSyncRule().getNeedLoadAndUpdateDestinations(nodeIds, needLoad, new HashMap<SourceKey, List<SegmentDestination>>());
        Iterator<Set<SegmentKey>> it = target.values().iterator();
        int total = 0;
        while (it.hasNext()) {
            // 每个节点都有
            int size = it.next().size();
            assertTrue(size > 0);
            if (nodeIds.size() <= 3) {
                assertEquals(size, needLoad.size());
            } else {
                assertTrue(size < needLoad.size());
            }
            total += size;
        }
        assertEquals(total, (nodeIds.size() > 3 ? 3 : nodeIds.size()) * needLoad.size());
    }
}