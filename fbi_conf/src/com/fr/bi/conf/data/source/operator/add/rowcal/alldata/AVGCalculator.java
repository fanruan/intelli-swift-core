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
public class AVGCalculator implements AllDataCalculator {
	
	public static AVGCalculator INSTANCE = new AVGCalculator();

	@Override
	public double get(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
		return SumCalculator.INSTANCE.get(ti, key, gvi)/CountCalculator.INSTANCE.get(ti, key, gvi);
	}

}