package com.fr.swift.result.node.resultset;

import com.fr.swift.base.meta.SwiftMetaDataEntity;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

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
        assertNotNull(rs.getMerger());
    }

    @Test
    public void convert() {
        try {
            rs.convert(new SwiftMetaDataEntity());
            fail();
        } catch (Exception ignored) {
        }
    }
}