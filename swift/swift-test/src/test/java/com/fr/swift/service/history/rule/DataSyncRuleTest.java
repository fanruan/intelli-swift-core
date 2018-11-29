package com.fr.swift.service.history.rule;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.handler.history.rule.DefaultDataSyncRule;
import com.fr.swift.test.Preparer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/7/16
 */
@RunWith(Parameterized.class)
public class DataSyncRuleTest {

    private Set<String> nodeIds;
    private Set<SegmentKey> needLoad;

    public DataSyncRuleTest(Set<String> nodeIds, Set<SegmentKey> needLoad) {
        this.nodeIds = nodeIds;
        this.needLoad = needLoad;
    }

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(getClass());
    }

    @Parameterized.Parameters
    public static List<Object[]> randomParams() {
        Map<String, List<SegmentKey>> needLoad = new HashMap<String, List<SegmentKey>>();
        needLoad.put("tableA", new ArrayList<SegmentKey>(100));
        for (int j = 0; j < 100; j++) {
            needLoad.get("tableA").add(new SegmentKeyBean("tableA", URI.create("uri_" + j), j, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        }
        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int nodeCount = (int) (1 + Math.random() * 100);
            Set<String> nodeIds = new HashSet<>();
            for (int j = 0; j < nodeCount; j++) {
                nodeIds.add("cluster_" + j);
            }
            result.add(new Object[]{nodeIds, needLoad});
        }
        return result;
    }

    @Test
    public void calculate() {
        Map<String, List<SegmentKey>> needloadMap = new HashMap<>();
        for (SegmentKey segmentKey : needLoad) {
            if (!needloadMap.containsKey(segmentKey.getTable().getId())) {
                needloadMap.put(segmentKey.getTable().getId(), new ArrayList<>());
            }
            needloadMap.get(segmentKey.getTable().getId()).add(segmentKey);
        }

        System.out.println("NodeSize： " + nodeIds.size() + " SegCount: " + needloadMap.get("tableA").size());
        Map<String, Set<SegmentKey>> target = new DefaultDataSyncRule().calculate(nodeIds, needLoad, new HashMap<String, List<SegmentDestination>>());
        Iterator<Set<SegmentKey>> it = target.values().iterator();
        int total = 0;

        while (it.hasNext()) {
            // 每个节点都有
            int size = it.next().size();
            assertTrue(size > 0);
            if (nodeIds.size() <= 3) {
                assertEquals(size, needloadMap.get("tableA").size());
            } else {
                assertTrue(size < needloadMap.get("tableA").size());
            }
            total += size;
        }
        assertEquals(total, (nodeIds.size() > 3 ? 3 : nodeIds.size()) * needloadMap.get("tableA").size());
    }
}