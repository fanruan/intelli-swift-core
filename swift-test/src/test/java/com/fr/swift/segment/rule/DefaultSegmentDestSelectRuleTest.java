package com.fr.swift.segment.rule;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Schema;
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
import java.util.List;
import java.util.Map;
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

    public DefaultSegmentDestSelectRuleTest(Set<String> nodeIds, Map<String, List<SegmentKey>> needLoad) {
        System.out.println("NodeSize： " + nodeIds.size() + " SegCount: " + needLoad.get("tableA").size());
        HashMap<String, List<SegmentDestination>> dest = new HashMap<>();
        new DefaultDataSyncRule().calculate(nodeIds, needLoad, dest);
        selectDestination = dest.get("tableA");
    }

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(getClass());
    }

    @Parameterized.Parameters
    public static List<Object[]> randomParams() {
        List<Object[]> result = new ArrayList<>();
        Map<String, List<SegmentKey>> needLoad = new HashMap<String, List<SegmentKey>>();
        needLoad.put("tableA", new ArrayList<SegmentKey>(100));
        for (int j = 0; j < 100; j++) {
            needLoad.get("tableA").add(new SegmentKeyBean("tableA", URI.create("uri_" + j), j, Types.StoreType.FINE_IO, Schema.CUBE));
        }
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
    public void selectDestination() {
        List<SegmentDestination> dest = new DefaultSegmentDestSelectRule().selectDestination(selectDestination);
        assertEquals(dest.size(), 100);
        for (int i = 0; i < dest.size() - 1; i++) {
            assertTrue(dest.get(i).getOrder() < dest.get(i + 1).getOrder());
            assertTrue(dest.get(i).getOrder() + 1 == dest.get(i + 1).getOrder());
        }
    }
}