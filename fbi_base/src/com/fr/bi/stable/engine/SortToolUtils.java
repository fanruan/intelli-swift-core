package com.fr.bi.stable.engine;

/**
 * Created by 小灰灰 on 2016/8/1.
 */
public class SortToolUtils {
    private static final long N_LOG_N = 100;
    private static final long GROUP_COUNT = 3;

    public static SortTool getSortTool(int groupSize, int gviCount){
        if (gviCount <= 1){
            return SortTool.DIRECT;
        }
        //尽量避免下面的log，/等数学运算
        if (groupSize < gviCount){
            return SortTool.INT_ARRAY;
        }
        double treeMap = gviCount * Math.log(gviCount) * N_LOG_N;
        double intArray = groupSize * GROUP_COUNT ;
        return treeMap < intArray ? SortTool.TREE_MAP : SortTool.INT_ARRAY;
    }

}
