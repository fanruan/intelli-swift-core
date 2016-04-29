/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 *
 */
public class AvgLine implements CalLineGetter {
	
	public static final AvgLine INSTANCE = new AvgLine();

	@Override
	public Double getCalLine(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
		if(gvi == null){
			return null;
		}
		double sum = ti.getSUMValue(gvi, key);
		long count = gvi.getRowsCountWithData();
		return sum/count;
	}

}