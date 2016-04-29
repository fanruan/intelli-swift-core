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
public interface CubeIntegerDataCalculator {

    int calculate(ICubeTableService tableGetterService, BIKey key, GroupValueIndex range);

}