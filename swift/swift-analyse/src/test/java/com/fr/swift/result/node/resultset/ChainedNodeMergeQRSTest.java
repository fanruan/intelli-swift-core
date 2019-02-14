package com.fr.swift.result.node.resultset;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class ChainedNodeMergeQRSTest {

    private ChainedNodeMergeQRS rs;

    @Before
    public void setUp() throws Exception {
        int fetchSize = 10;
        rs = new ChainedNodeMergeQRS(fetchSize, new boolean[0], new ArrayList<NodeMergeQRS<GroupNode>>(),
                new ArrayList<Aggregator>(), new ArrayList<Comparator<GroupNode>>(),
                new NodeQueryResultSetMerger(fetchSize, new boolean[0], new ArrayList<Aggregator>(),
                        new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>()));
    }

    @Test
    public void getMerger() {
        assertNotNull(rs.<QueryResultSet<Pair<GroupNode, List<Map<Integer, Object>>>>>getMerger());
    }

    @Test
    public void convert() {
        try {
            rs.convert(new SwiftMetaDataBean());
            fail();
        } catch (Exception ignored) {
        }
    }
}