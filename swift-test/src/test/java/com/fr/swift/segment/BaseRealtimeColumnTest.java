package com.fr.swift.segment;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * @author anchore
 * @date 2018/6/13
 */
public abstract class BaseRealtimeColumnTest<T> {
    Random r = new Random(hashCode());
    T[] data1, data2;
    static final int BOUND = 1000;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    abstract Column<T> getColumn();

    @Test
    public void test() {
        for (int i = 0; i < data1.length; i++) {
            getColumn().getDetailColumn().put(i, data1[i]);
        }

        DictionaryEncodedColumn<T> dict = getColumn().getDictionaryEncodedColumn();
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(getColumn().getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }

        for (int i = 0; i < data2.length; i++) {
            getColumn().getDetailColumn().put(data2.length + i, data2[i]);
        }

        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(getColumn().getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }
        for (int i = 0; i < data2.length; i++) {
            Assert.assertEquals(data2[i], dict.getValue(dict.getIndexByRow(data2.length + i)));
            Assert.assertTrue(getColumn().getBitmapIndex().getBitMapIndex(dict.getIndex(data2[i])).contains(data2.length + i));
        }

    }
}

