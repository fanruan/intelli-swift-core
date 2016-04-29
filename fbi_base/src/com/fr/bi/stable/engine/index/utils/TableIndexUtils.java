package com.fr.bi.stable.engine.index.utils;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.*;

/**
 * Created by GUY on 2015/4/8.
 */
public class TableIndexUtils {

    public static Object[] getDistinctValue(final ICubeTableService ti, BIField field) {
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

    /**
     * 获取关联后的nullGVI
     *
     * @param start
     * @param end
     * @param relations
     * @return
     */
    public static GroupValueIndex createLinkNullGVI(ICubeTableService start, ICubeTableService end, List<BITableSourceRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        BIKey columnIndex = start.getColumnIndex(relations.get(0).getPrimaryKey().getFieldName());
        GroupValueIndex currentIndex = start.getNullGroupValueIndex(columnIndex);
        final ICubeTableIndexReader reader = start.ensureBasicIndex(relations);
        if (reader == null) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        long endRowcount = end.getRowCount();
        GroupValueIndex gvi = GVIUtils.getTableLinkedOrGVI(currentIndex, reader);
        if (gvi == null) {
            gvi = GVIFactory.createAllEmptyIndexGVI();
        }
        return gvi;
    }

    public static GroupValueIndex createLinkNullGVI(ICubeTableService start, List<BITableSourceRelation> relations, ICubeDataLoader loader) {
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        ICubeTableService eti = loader.getTableIndex(relations.get(relations.size() - 1).getForeignKey());
        return createLinkNullGVI(start, eti, relations);
    }
}