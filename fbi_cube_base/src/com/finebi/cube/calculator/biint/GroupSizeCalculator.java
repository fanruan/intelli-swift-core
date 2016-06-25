/**
 *
 */
package com.finebi.cube.calculator.biint;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 */
public class GroupSizeCalculator implements CubeIntegerDataCalculator {

    public static GroupSizeCalculator INSTANCE = new GroupSizeCalculator();

    @Override
    public int calculate(ICubeTableService tableGetterService, BIKey key, GroupValueIndex range) {
        return GroupValueCalculator.INSTANCE.calculate(tableGetterService, key, range);
    }

}