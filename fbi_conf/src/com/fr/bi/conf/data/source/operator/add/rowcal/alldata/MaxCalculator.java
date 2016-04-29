/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.alldata;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 *
 */
public class MaxCalculator implements AllDataCalculator {
	
	public static MaxCalculator INSTANCE = new MaxCalculator();

	@Override
	public double get(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
		return ti.getMAXValue(gvi, key);
	}

}