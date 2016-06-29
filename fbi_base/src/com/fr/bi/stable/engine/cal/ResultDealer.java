/**
 * 
 */
package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * @author Daniel
 *
 */
public interface ResultDealer {

	/**
	 * @param ti
	 * @param currentIndex
	 */
	void dealWith(ICubeTableService ti, GroupValueIndex currentIndex);

}