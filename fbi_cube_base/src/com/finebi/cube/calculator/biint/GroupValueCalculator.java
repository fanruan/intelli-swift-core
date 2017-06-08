package com.finebi.cube.calculator.biint;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.cal.SortToolUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.stable.collections.map.IntSet;

import java.util.ArrayList;

/**
 * This class created on 2016/4/14.
 *
 * @author Connery
 * @since 4.0
 */
public class GroupValueCalculator {

    public static GroupValueCalculator INSTANCE = new GroupValueCalculator();

    public int calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        final ICubeValueEntryGetter getter = tableGetterService.getValueEntryGetter(key, new ArrayList<BITableSourceRelation>());
        //pony 如果行数等于groupsize那就是唯一的
        if (getter.getGroupSize() == tableGetterService.getRowCount()){
            return range.getRowsCountWithData();
        }
        switch (SortToolUtils.getGroupValueTool(getter.getGroupSize(), range.getRowsCountWithData())){
            case TREE_MAP:
                return getByTreeMap(range, getter);
            case INT_ARRAY:
                return getByIntArray(range, getter);
            case DIRECT:
                return 1;
            //pony default是EMPTY
            default:
                return 0;
        }
    }


    private int getByTreeMap(GroupValueIndex range, final ICubeValueEntryGetter getter) {
        final IntSet set = new IntSet();
        range.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                set.add(getter.getPositionOfGroupByRow(row));
            }
        });
        return set.size;
    }

    private int getByIntArray(GroupValueIndex range, final ICubeValueEntryGetter getter) {
        final int[] groups = new int[getter.getGroupSize()];
        range.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                groups[getter.getPositionOfGroupByRow(row)] = 1;
            }
        });
        int size = 0;
        for (int i = 0; i < groups.length; i++){
            if (groups[i] == 1){
                size ++;
            }
        }
        return size;
    }
}
