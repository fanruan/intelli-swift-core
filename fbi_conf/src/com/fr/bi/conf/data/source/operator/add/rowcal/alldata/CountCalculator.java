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
public class CountCalculator implements AllDataCalculator {
	
	public static CountCalculator INSTANCE = new CountCalculator();

	@Override
	public double get(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
		return gvi.getRowsCountWithData();
	}

}