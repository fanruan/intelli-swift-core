package com.fr.bi.stable.engine.index.utils;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by GUY on 2015/4/8.
 */
public class TableIndexUtils {

    public static Object[] getDistinctValue(final ICubeTableService ti, BusinessField field) {
        Set<Object> values = new HashSet<Object>();
        BIKey index = ti.getColumnIndex(field);
        ICubeColumnIndexReader getter = ti.loadGroup(index);
        Iterator it = getter.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            values.add(entry.getKey());
        }
        return values.toArray();
    }

    /**
     * 根据索引获取不重复值数值
     *
     * @param ti    TableIndex
     * @param gvi   索引们
     * @param index 列
     * @return 值数组 不重复
     */
    public static Object[] getValueFromGvi(final ICubeTableService ti, final BIKey index, final GroupValueIndex[] gvi) {
        final Set<Object> values = new HashSet<Object>();
        for (int i = 0; i < gvi.length; i++) {
            if (gvi[i] != null) {
                gvi[i].Traversal(new SingleRowTraversalAction() {

                    @Override
                    public void actionPerformed(int rowIndices) {
                        Object v = ti.getRowValue(index, rowIndices);
                        if (v != null && (!values.contains(v))) {
                            values.add(v);
                        }
                    }

                });
            }
        }
        return values.toArray();
    }


}