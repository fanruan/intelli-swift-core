package com.fr.bi.stable.engine;
import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2016/8/12.
 */
public class SortToolUtilsTest extends TestCase{
    public void testGetSortTool() {
        assertEquals(SortToolUtils.getSortTool(10, 1), SortTool.DIRECT);
        assertEquals(SortToolUtils.getSortTool(1000000, 10), SortTool.TREE_MAP);
        assertEquals(SortToolUtils.getSortTool(100, 1000), SortTool.INT_ARRAY);
    }

}