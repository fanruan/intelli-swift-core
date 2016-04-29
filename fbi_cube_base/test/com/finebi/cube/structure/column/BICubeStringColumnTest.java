package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.tools.BICubeResourceLocationTestTool;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeStringColumnTest extends BICubeColumnBasicTest<String> {
    @Override
    public ICubeColumnEntityService<String> getTestTarget() {
        return new BICubeStringColumn(BICubeResourceLocationTestTool.getBasic("testStringCubeColumn"));
    }

    @Override
    public List<String> getListData() {
        List<String> lists = new ArrayList<String>();
        lists.add("b");
        lists.add("e");
        lists.add("d");
        lists.add("a");
        lists.add("f");
        lists.add("c");

        return lists;
    }

    @Override
    public void checkCubeColumnGroupSort(ICubeColumnEntityService<String> column) {
        assertEquals(column.getGroupValue(0), "a");
        assertEquals(column.getGroupValue(1), "b");
        assertEquals(column.getGroupValue(2), "c");
    }


    public void testGetGroupByPosition() {
        try {
            assertEquals(column.getPositionOfGroup("a"), 0);
            assertEquals(column.getPositionOfGroup("b"), 1);
            assertEquals(column.getPositionOfGroup("c"), 2);

        } catch (BIResourceInvalidException e) {
            assertTrue(false);
        }

    }
}
