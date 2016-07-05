package com.fr.bi.stable.engine.index.utils;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.cal.AllSingleDimensionGroup;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GUY on 2015/4/8.
 */
public class TableIndexUtils {

    /**
     * 根据索引获取不重复值数值
     *
     * @param ti    TableIndex
     * @param gvi   索引们
     * @param index 列
     * @return 值数组 不重复
     */
    public static Object[] getValueFromGvi(final ICubeTableService ti, final BIKey index, final GroupValueIndex[] gvi) {
       return getValueFromGvi(ti, index, gvi, new ArrayList<BITableSourceRelation>());
    }


    public static Object[] getValueFromGvi(final ICubeTableService ti, final BIKey index, final GroupValueIndex[] gvi, List<BITableSourceRelation> relationList) {
        Set<Object> values = new HashSet<Object>();
        final ICubeValueEntryGetter getter = ti.getValueEntryGetter(index, relationList);
        for (int i = 0; i < gvi.length; i++) {
            values.addAll(AllSingleDimensionGroup.getAllValue(gvi[0], getter));
        }
        return values.toArray();
    }


}