package com.finebi.cube.calculator.biint;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.cal.AllSingleDimensionGroup;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * This class created on 2016/4/14.
 *
 * @author Connery
 * @since 4.0
 */
public class GroupValueCalculator {

    public static GroupValueCalculator INSTANCE = new GroupValueCalculator();

    public int calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        CountDealer dealer = new CountDealer();
        AllSingleDimensionGroup.run(range, tableGetterService, key, dealer);
        return dealer.getCount();
    }

    private class  CountDealer implements ResultDealer {

        private int count = 0;
        @Override
        public void dealWith(ICubeTableService ti, GroupValueIndex currentIndex) {
            count ++;
        }

        public int getCount() {
            return count;
        }
    }

}
