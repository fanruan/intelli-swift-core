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
public class SumCalculator implements AllDataCalculator {
	
	public static SumCalculator INSTANCE = new SumCalculator();

	@Override
	public double get(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
		return ti.getSUMValue(gvi, key);
	}

}