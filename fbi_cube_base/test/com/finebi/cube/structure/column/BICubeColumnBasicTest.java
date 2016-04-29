package com.finebi.cube.structure.column;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeColumnBasicTest<T> extends TestCase {
    protected ICubeColumnEntityService<T> column;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        column = getTestTarget();
        List<T> lists = getListData();
        Collections.sort(lists, column.getGroupComparator());
        for (int i = 0; i < lists.size(); i++) {
            column.addGroupValue(i, lists.get(i));
        }
        column.recordSizeOfGroup(lists.size());
    }

    public void testCubeColumnGroupSort() {

        checkCubeColumnGroupSort(column);
    }

    public abstract ICubeColumnEntityService<T> getTestTarget();

    public abstract List<T> getListData();

    public abstract void checkCubeColumnGroupSort(ICubeColumnEntityService<T> column);


}
