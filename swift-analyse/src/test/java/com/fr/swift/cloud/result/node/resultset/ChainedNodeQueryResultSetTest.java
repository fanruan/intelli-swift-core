package com.fr.swift.cloud.result.node.resultset;

import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.result.BaseNodeQueryResultSet;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by lyon on 2019/1/8.
 */
public class ChainedNodeQueryResultSetTest {

    static int fetchSize = 10;

    static QueryResultSet<SwiftNode> create() {
        return new BaseNodeQueryResultSet(fetchSize) {

            private boolean hasNextPage = true;

            @Override
            public SwiftNode getPage() {
                GroupNode root = new GroupNode();
                GroupNode child = new GroupNode(0, "a");
                root.addChild(child);
                hasNextPage = false;
                return root;
            }

            @Override
            public boolean hasNextPage() {
                return hasNextPage;
            }
        };
    }

    private ChainedNodeQueryResultSet rs;

    @Before
    public void setUp() {
        rs = new ChainedNodeQueryResultSet(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, create());
    }

    @Test
    public void getPage() {
        rs = new ChainedNodeQueryResultSet(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, create());
        assertTrue(rs.hasNextPage());
        assertNotNull(rs.getPage());
        assertFalse(rs.hasNextPage());
    }

    @Test
    public void convert() {
        rs = new ChainedNodeQueryResultSet(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, create());
        assertNotNull(rs.convert(new SwiftMetaDataEntity()));
    }

    @Test
    public void getFetchSize() {
        assertEquals(fetchSize, rs.getFetchSize());
    }
}