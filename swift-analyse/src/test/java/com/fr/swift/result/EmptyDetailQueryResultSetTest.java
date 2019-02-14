package com.fr.swift.result;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by lyon on 2019/1/8.
 */
public class EmptyDetailQueryResultSetTest {

    private EmptyDetailQueryResultSet rs;

    @Before
    public void setUp() throws Exception {
        rs = new EmptyDetailQueryResultSet();
    }

    @Test
    public void getPage() {
        assertFalse(rs.hasNextPage());
    }

    @Test
    public void convert() {
        assertEquals(EmptyResultSet.INSTANCE, rs.convert(null));
    }
}