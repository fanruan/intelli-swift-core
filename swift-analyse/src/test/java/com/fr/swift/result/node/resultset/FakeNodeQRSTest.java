package com.fr.swift.result.node.resultset;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.SwiftRowOperator;
import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class FakeNodeQRSTest {

    private FakeNodeQRS rs;

    @Before
    public void setUp() throws Exception {
        rs = new FakeNodeQRS(new SwiftRowOperator<Row>() {
            @Override
            public List<Row> operate(SwiftNode node) {
                return IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(node));
            }
        }, ChainedNodeQRSTest.create());
    }

    @Test
    public void getPage() {
        assertTrue(rs.hasNextPage());
        try {
            rs.getPage();
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void getFetchSize() {
        assertEquals(ChainedNodeQRSTest.fetchSize, rs.getFetchSize());
    }

    @Test
    public void getMerger() {
        try {
            rs.getMerger();
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void convert() {
        assertNotNull(rs.convert(new SwiftMetaDataBean()));
    }
}