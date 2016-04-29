/**
 *
 */
package com.finebi.cube.calculator.bilong;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 */
public interface CubeLongDataCalculator {

    long calculate(ICubeTableService tableGetterService, BIKey key, GroupValueIndex range);

}