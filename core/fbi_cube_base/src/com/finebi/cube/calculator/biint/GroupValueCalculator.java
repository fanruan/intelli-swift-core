package com.finebi.cube.calculator.biint;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
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
        if (range.getRowsCountWithData() == 1){
            return 1;
        }
        final IntSet set = new IntSet();
        range.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                set.add(getter.getPositionOfGroupByRow(row));
            }
        });
        return set.size;
    }


}
