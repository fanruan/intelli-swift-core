/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;

/**
 * @author Daniel
 *
 */
public class NumberLargeCLFilter extends NumberCalculateLineFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3026020133625295929L;

	/**
	 * @param t
	 */
    public NumberLargeCLFilter() {
		super(Large.INSTANCE);
	}

	@Override
	public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
		return false;
	}
}