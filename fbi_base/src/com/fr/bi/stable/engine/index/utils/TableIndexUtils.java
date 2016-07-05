package com.fr.bi.stable.engine.index.utils;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

import java.util.HashSet;
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
       return getValueFromGvi(ti, index, gvi, new ConnectionRowGetter());
    }


    public static Object[] getValueFromGvi(final ICubeTableService ti, final BIKey index, final GroupValueIndex[] gvi, final ConnectionRowGetter connectionRowGetter) {
        final Set<Object> values = new HashSet<Object>();
        final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(index);
        for (int i = 0; i < gvi.length; i++) {
            if (gvi[i] != null) {
//                AllSingleDimensionGroup.run(gvi[i], ti, index, new ResultDealer() {
//                    @Override
//                    public void dealWith(ICubeTableService ti, GroupValueIndex currentIndex) {
//                        currentIndex.BrokenableTraversal(new BrokenTraversalAction() {
//                            @Override
//                            public boolean actionPerformed(int rowIndices) {
//                                Integer row = connectionRowGetter.getConnectedRow(rowIndices);
//                                Object v = row == null ? null : getter.getValue(row);
//                                if (v != null && (!values.contains(v))) {
//                                    values.add(v);
//                                }
//                                return true;
//                            }
//                        });
//                    }
//                });
                gvi[i].Traversal(new SingleRowTraversalAction() {

                    @Override
                    public void actionPerformed(int rowIndices) {
                        Integer row = connectionRowGetter.getConnectedRow(rowIndices);
                        Object v = row == null ? null : getter.getValue(row);
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