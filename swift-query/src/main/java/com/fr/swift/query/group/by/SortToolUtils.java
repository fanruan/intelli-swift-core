package com.fr.swift.query.group.by;

import com.fr.swift.setting.PerformancePlugManager;

/**
 * @author 小灰灰
 * @date 2016/8/1
 */
public class SortToolUtils {
    private static final long N_LOG_N = 100;
    private static final long GROUP_COUNT = 3;
    private static final int RESORT_COUNT = PerformancePlugManager.getInstance().getReIndexRowCount();

    public static SortTool getSortTool(int groupSize, int gviCount) {
        if (gviCount == 1) {
            return SortTool.DIRECT;
        }
        if (gviCount == 0) {
            return SortTool.EMPTY;
        }
        //尽量避免下面的log，/等数学运算
        if (groupSize < gviCount) {
            return getIntArray(gviCount);
        }
        double treeMap = gviCount * Math.log(gviCount) * N_LOG_N;
        double intArray = groupSize * GROUP_COUNT;
        return treeMap < intArray ? getTreeMap(gviCount) : getIntArray(gviCount);
    }

    private static SortTool getIntArray(int gviCount) {
        return gviCount <= RESORT_COUNT ? SortTool.INT_ARRAY_RESORT : SortTool.INT_ARRAY;
    }

    private static SortTool getTreeMap(int gviCount) {
        return gviCount <= RESORT_COUNT ? SortTool.TREE_MAP_RESORT : SortTool.TREE_MAP;
    }

    public static SortTool getGroupValueTool(int groupSize, int gviCount) {
        if (gviCount == 1) {
            return SortTool.DIRECT;
        }
        if (gviCount == 0) {
            return SortTool.EMPTY;
        }
        //尽量避免下面的log，/等数学运算
        if (groupSize < gviCount) {
            return SortTool.INT_ARRAY;
        }
        double treeMap = gviCount * Math.log(gviCount) * N_LOG_N;
        double intArray = groupSize * GROUP_COUNT;
        return treeMap < intArray ? SortTool.TREE_MAP : SortTool.INT_ARRAY;
    }

}
