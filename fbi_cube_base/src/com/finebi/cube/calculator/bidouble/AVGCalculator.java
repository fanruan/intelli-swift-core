/**
 * 
 */
package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.calculator.biint.GroupSizeCalculator;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 *
 */
public class AVGCalculator implements CubeDoubleDataCalculator{
	
	public static AVGCalculator INSTANCE = new AVGCalculator();

	@Override
	public double calculate(ICubeTableService tableGetterService, BIKey key, GroupValueIndex range) {
		return SumCalculator.INSTANCE.calculate(tableGetterService, key, range)/ GroupSizeCalculator.INSTANCE.calculate(tableGetterService, key, range);
	}

}