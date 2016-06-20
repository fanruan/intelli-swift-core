/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.index;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.Operator;
import com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line.CalLineGetter;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

/**
 * @author Daniel
 *
 */
public class NumberIndexCreater {
	
	private ICubeTableService ti;
	
	private Operator op;
	
	private BIKey key;
	
	private CalLineGetter getter;
	
	public NumberIndexCreater(ICubeTableService ti,
				Operator op,
				BIKey key,
				CalLineGetter getter){
		this.ti = ti;
		this.op = op;
		this.key = key;
		this.getter = getter;
	}
	
	
	public GroupValueIndex createFilterGvi(GroupValueIndex gvi){
		final double avg = getter.getCalLine(ti, key, gvi);
		final GroupValueIndex index = GVIFactory.createAllEmptyIndexGVI();
		gvi.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int data) {
                Object v = ti.getRow(key, data);
				if(v != null && op.Compare(((Number)v).doubleValue(), avg)){
					index.addValueByIndex(data);
				}
			}
		});
		return index;
	}
	
}