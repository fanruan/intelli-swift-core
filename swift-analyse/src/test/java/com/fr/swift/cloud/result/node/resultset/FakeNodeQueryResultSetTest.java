package com.fr.swift.cloud.result.node.resultset;

import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.result.SwiftRowOperator;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.structure.iterator.IteratorUtils;
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
public class FakeNodeQueryResultSetTest {

    private FakeNodeQueryResultSet rs;

    @Before
    public void setUp() {
        rs = new FakeNodeQueryResultSet(new SwiftRowOperator<Row>() {
            @Override
            public List<Row> operate(SwiftNode node) {
                return IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(node));
            }
        }, ChainedNodeQueryResultSetTest.create());
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
        assertEquals(ChainedNodeQueryResultSetTest.fetchSize, rs.getFetchSize());
    }

    @Test
    public void convert() {
        assertNotNull(rs.convert(new SwiftMetaDataEntity()));
    }
}