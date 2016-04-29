package com.finebi.cube.calculator;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/4/14.
 *
 * @author Connery
 * @since 4.0
 */
public class GroupValueCalculator {

    public static GroupValueCalculator INSTANCE = new GroupValueCalculator();

    public Set calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        final Set<Object> result = new HashSet<Object>();
        SingleRowTraversalAction ss = new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object v = tableGetterService.getRow(key, row);
                //D:null值不做统计
                if (v != null) {
                    result.add(v);
                }
            }
        };
        range.Traversal(ss);
        return result;
    }

}