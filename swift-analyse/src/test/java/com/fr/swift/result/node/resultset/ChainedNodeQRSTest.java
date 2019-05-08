package com.fr.swift.result.node.resultset;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.BaseNodeQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.qrs.QueryResultSet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class ChainedNodeQRSTest {

    static int fetchSize = 10;

    static QueryResultSet<SwiftNode> create() {
        return new BaseNodeQRS(fetchSize) {

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

    private ChainedNodeQRS rs;

    @Before
    public void setUp() throws Exception {
        rs = new ChainedNodeQRS(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, create());
    }

    @Test
    public void getPage() {
        rs = new ChainedNodeQRS(new SwiftNodeOperator() {
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
        rs = new ChainedNodeQRS(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, create());
        assertNotNull(rs.convert(new SwiftMetaDataBean()));
    }

    @Test
    public void getFetchSize() {
        assertEquals(fetchSize, rs.getFetchSize());
    }

    @Test
    public void getMerger() {
        try {
            rs.getMerger();
            fail();
        } catch (Exception ignored) {
        }
    }
}