package com.fr.swift.query.result.serialize;

import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class DetailSerializableQRSTest {

    private int fetchSize = 1;
    private int rowCount = 3;
    private SerializedDetailQueryResultSet qrs;

    @Before
    public void setUp() {
        qrs = new SerializedDetailQueryResultSet(fetchSize, rowCount, new ArrayList<Row>(), false);
    }

    @Test
    public void getRowCount() {
        assertEquals(rowCount, qrs.getRowCount());
    }

    @Test
    public void setInvoker() {
        List<Row> page = new ArrayList<Row>(Collections.singleton(new ListBasedRow(Collections.singletonList("a"))));
        final SerializedDetailQueryResultSet next = new SerializedDetailQueryResultSet(fetchSize, rowCount, page, false);
        qrs = new SerializedDetailQueryResultSet(fetchSize, rowCount, page, true);
        qrs.setInvoker(new BaseSerializedQueryResultSet.SyncInvoker() {
            @Override
            public BaseSerializedQueryResultSet<?> invoke() {
                return next;
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
    public void getPage() {
        assertTrue(qrs.hasNextPage());
        assertNotNull(qrs.getPage());
        assertFalse(qrs.hasNextPage());
    }

    @Test
    public void convert() {
        try {
            qrs.convert(new SwiftMetaDataEntity());
            fail();
        } catch (Exception ignored) {
        }
    }
}