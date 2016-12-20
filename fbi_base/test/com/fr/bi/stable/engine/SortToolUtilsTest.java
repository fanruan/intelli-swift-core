package com.fr.bi.stable.engine;
import com.fr.bi.stable.engine.cal.SortTool;
import com.fr.bi.stable.engine.cal.SortToolUtils;
import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2016/8/12.
 */
public class SortToolUtilsTest extends TestCase{
    public void testGetSortTool() {
        assertEquals(SortToolUtils.getSortTool(10, 1), SortTool.DIRECT);
        assertEquals(SortToolUtils.getSortTool(10, 10), SortTool.INT_ARRAY_RE_SORT);
        assertEquals(SortToolUtils.getSortTool(100000, 10), SortTool.TREE_MAP_RE_SORT);
        assertEquals(SortToolUtils.getSortTool(100000000, 10000), SortTool.TREE_MAP);
        assertEquals(SortToolUtils.getSortTool(100, 10000), SortTool.INT_ARRAY);
    }

}