package com.fr.swift.query.result.serialize;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.DetailQueryResultSetMerger;
import com.fr.swift.result.IDetailQueryResultSetMerger;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

    int fetchSize = 1;
    int rowCount = 3;
    private DetailSerializableQRS qrs;

    @Before
    public void setUp() throws Exception {
        qrs = new DetailSerializableQRS(fetchSize, rowCount, new DetailQueryResultSetMerger(fetchSize),
                new ArrayList<Row>(), false);
    }

    @Test
    public void getRowCount() {
        assertEquals(rowCount, qrs.getRowCount());
    }

    @Test
    public void setInvoker() {
        List<Row> page = new ArrayList<Row>(Collections.singleton(new ListBasedRow(Arrays.asList("a"))));
        IDetailQueryResultSetMerger merger = new DetailQueryResultSetMerger(fetchSize);
        final DetailSerializableQRS next = new DetailSerializableQRS(fetchSize, rowCount, merger, page, false);
        qrs = new DetailSerializableQRS(fetchSize, rowCount, merger, page, true);
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