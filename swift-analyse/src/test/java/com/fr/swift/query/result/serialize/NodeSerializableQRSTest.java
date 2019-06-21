package com.fr.swift.query.result.serialize;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.resultset.NodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class NodeSerializableQRSTest {

    private int fetchSize = 1;
    private NodeSerializableQRS qrs;

    @Before
    public void setUp() {
        qrs = new NodeSerializableQRS(fetchSize, new NodeQueryResultSetMerger(fetchSize, new boolean[0],
                new ArrayList<Aggregator>(), new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>()),
                new GroupPage(null, null), false);
    }

    @Test
    public void setInvoker() {
        GroupPage page = new GroupPage(new GroupNode(), null);
        QueryResultSetMerger<QueryResultSet<GroupPage>> merger = new NodeQueryResultSetMerger(fetchSize, new boolean[0],
                new ArrayList<Aggregator>(), new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>());
        final NodeSerializableQRS next = new NodeSerializableQRS(fetchSize, merger, page, false);
        qrs = new NodeSerializableQRS(fetchSize, merger, page, true);
        qrs.setInvoker(new BaseSerializableQRS.SyncInvoker() {
            @Override
            public <D, T extends BaseSerializableQRS<D>> T invoke() {
                return (T) next;
            }
        });
        assertTrue(qrs.hasNextPage());
        assertNotNull(qrs.getPage());
        assertTrue(qrs.hasNextPage());
        assertNotNull(qrs.getPage());
        assertFalse(qrs.hasNextPage());
    }

    @Test
    public void getFetchSize() {
        assertEquals(fetchSize, qrs.getFetchSize());
    }

    @Test
    public void getMerger() {
        assertNotNull(qrs.getMerger());
    }

    @Test
    public void getPage() {
        assertTrue(qrs.hasNextPage());
        assertNotNull(qrs.getPage());
        assertFalse(qrs.hasNextPage());
    }

    @Test
    public void convert() {
        try {
            qrs.convert(new SwiftMetaDataBean());
            fail();
        } catch (Exception ignored) {
        }
    }
}