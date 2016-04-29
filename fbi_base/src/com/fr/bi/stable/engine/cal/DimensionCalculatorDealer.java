/**
 * 
 */
package com.fr.bi.stable.engine.cal;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 *
 */
public class DimensionCalculatorDealer implements ResultDealer {
	
	private BIKey key;
	
	private ResultDealer next;
	
	public DimensionCalculatorDealer(BIKey key){
		this.key = key;
	}

	@Override
	public void dealWith(ICubeTableService ti, GroupValueIndex currentIndex, final int startCol) {
		AllSingleDimensionGroup.run(currentIndex, ti, this.key, next, startCol);
	}
	
	public void setNext(ResultDealer next){
		this.next = next;
	}

}