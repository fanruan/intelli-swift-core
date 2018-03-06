package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.Comparator;
import java.util.Random;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;

/**
 * @author anchore
 * @date 2017/11/10
 */
public abstract class BaseDictColumnTest<T> extends TestCase {

    static final String BASE_PATH = CUBES_PATH;
    Random r = new Random();
    T[] values;
    Comparator<T> c;

    abstract DictionaryEncodedColumn<T> getDictColumn();

    public void testPutValueThenGet() {
        DictionaryEncodedColumn<T> dictColumn = getDictColumn();
        for (int i = 0; i < values.length; i++) {
            dictColumn.putValue(i, values[i]);
        }
        dictColumn.putSize(values.length);
        reset(dictColumn);

        dictColumn = getDictColumn();
        for (int i = 0; i < values.length; i++) {
            assertEquals(i, dictColumn.getIndex(values[i]));
            assertEquals(values[i], dictColumn.getValue(i));
        }
    }

    public void testPutSizeThenGet() {
        DictionaryEncodedColumn<T> dictColumn = getDictColumn();
        int size = r.nextInt(1000000000);
        dictColumn.putSize(size);
        reset(dictColumn);

        dictColumn = getDictColumn();
        assertEquals(size, dictColumn.size());
    }

    public void testPutIndexThenGet() {
        int size = 100000;
        DictionaryEncodedColumn<T> dictColumn = getDictColumn();
        int[] indices = r.ints(size, 0, size << 1).toArray();
        for (int i = 0; i < indices.length; i++) {
            dictColumn.putIndex(i, indices[i]);
        }
        reset(dictColumn);

        dictColumn = getDictColumn();
        for (int i = 0; i < indices.length; i++) {
            assertEquals(indices[i], dictColumn.getIndexByRow(i));
        }
    }

    /**
     * release之后，discovery还保留着，清一遍，免得影响别的test方法
     */
    static <T> void reset(DictionaryEncodedColumn<T> column) {
        column.release();
        ResourceDiscoveryImpl.getInstance().clear();
    }

}