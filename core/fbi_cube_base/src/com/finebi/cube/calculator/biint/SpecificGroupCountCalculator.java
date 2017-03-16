/**
 *
 */
package com.finebi.cube.calculator.biint;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.cal.AllSingleDimensionGroup;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * This class created on 2016/6/24.
 *
 * @author Connery
 * @since 4.0
 */
public class SpecificGroupCountCalculator implements CubeIntegerDataCalculator {

    public static SpecificGroupCountCalculator INSTANCE = new SpecificGroupCountCalculator();

    @Override
    public int calculate(final ICubeTableService tableGetterService, final BIKey key, GroupValueIndex range) {
        return range.getRowsCountWithData();
    }

}