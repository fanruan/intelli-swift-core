package com.fr.swift.query.group;

import com.fr.swift.query.group.by.SortTool;
import com.fr.swift.query.group.by.SortToolUtils;
import junit.framework.TestCase;

public class SortToolUtilsTest extends TestCase {
    public void testGetSortTool() {
        assertEquals(SortToolUtils.getSortTool(10, 1), SortTool.DIRECT);
        assertEquals(SortToolUtils.getSortTool(10, 10), SortTool.INT_ARRAY_RE_SORT);
        assertEquals(SortToolUtils.getSortTool(100000, 10), SortTool.TREE_MAP_RE_SORT);
        assertEquals(SortToolUtils.getSortTool(100000000, 10000), SortTool.TREE_MAP);
        assertEquals(SortToolUtils.getSortTool(100, 10000), SortTool.INT_ARRAY);
    }
}