package com.fr.swift.segment;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.test.Preparer;
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
        Preparer.prepareCubeBuild(getClass());
    }

    @After
    public void tearDown() {
    }

    abstract Column<T> getColumn();

    @Test
    public void test() {
        Column<T> column = getColumn();
        for (int i = 0; i < data1.length; i++) {
            column.getDetailColumn().put(i, data1[i]);
        }

        column = getColumn();
        DictionaryEncodedColumn<T> dict = column.getDictionaryEncodedColumn();
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }

        column = getColumn();
        for (int i = 0; i < data2.length; i++) {
            column.getDetailColumn().put(data2.length + i, data2[i]);
        }

        column = getColumn();
        dict = column.getDictionaryEncodedColumn();
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }
        for (int i = 0; i < data2.length; i++) {
            Assert.assertEquals(data2[i], dict.getValue(dict.getIndexByRow(data2.length + i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data2[i])).contains(data2.length + i));
        }

    }
}

