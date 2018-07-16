package com.fr.swift.service.history.rule;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.handler.history.rule.DataSyncRule;
import com.fr.swift.test.Preparer;
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

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/7/16
 */
@RunWith(Parameterized.class)
public class DataSyncRuleTest {

    private Set<String> exists;
    private Map<String, List<SegmentKey>> needLoad;

    public DataSyncRuleTest(Set<String> exists, Map<String, List<SegmentKey>> needLoad) {
        this.exists = exists;
        this.needLoad = needLoad;
    }

    @Parameterized.Parameters
    public static List<Object[]> randomParams() throws Exception {
        Preparer.prepareCubeBuild();
        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int nodeCount = (int) (1 + Math.random() * 10);
            Set<String> exists = new HashSet<>();
            for (int j = 0; j < nodeCount; j++) {
                exists.add("cluster_" + j);
            }
            Map<String, List<SegmentKey>> needLoad = new HashMap<String, List<SegmentKey>>();
            needLoad.put("tableA", new ArrayList<SegmentKey>(100));
            for (int j = 0; j < 100; j++) {
                needLoad.get("tableA").add(new SegmentKeyBean("tableA", URI.create("uri_" + j), j, Types.StoreType.FINE_IO));
            }
            result.add(new Object[]{exists, needLoad});
        }
        return result;
    }

    @Test
    public void calculate() {
        Map<String, Set<SegmentKey>> target = DataSyncRule.DEFAULT.calculate(exists, needLoad, new HashMap<String, List<SegmentDestination>>());
        Iterator<Set<SegmentKey>> it = target.values().iterator();
        while (it.hasNext()) {
            // 每个节点都有
            int size = it.next().size();
            assertTrue(size > 0);
        }
    }
}