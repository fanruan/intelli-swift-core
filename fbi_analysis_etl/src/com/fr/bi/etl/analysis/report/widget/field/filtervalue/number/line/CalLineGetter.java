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
public interface CalLineGetter {
	
	Double getCalLine(ICubeTableService ti, BIKey key, GroupValueIndex gvi);
	
}