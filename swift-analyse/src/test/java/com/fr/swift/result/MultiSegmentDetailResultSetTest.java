package com.fr.swift.result;

import com.fr.swift.base.meta.SwiftMetaDataEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class MultiSegmentDetailResultSetTest {

    private MultiSegmentDetailResultSet rs;

    @Before
    public void setUp() throws Exception {
        rs = new MultiSegmentDetailResultSet(100, 200, null, new DetailQueryResultSetMerger(100));
    }

    @Test
    public void getMerger() {
        assertNotNull(rs.getMerger());
    }

    @Test
    public void convert() {
        try {
            rs.convert(new SwiftMetaDataEntity());
        } catch (Exception e) {
            fail();
        }
    }
}