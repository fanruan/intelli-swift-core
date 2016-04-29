/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.fr.bi.common.BICoreService;
import com.finebi.cube.api.ICubeTableService;
import com.fr.json.JSONTransform;

/**
 * @author Daniel
 *
 */
public interface Expression extends JSONTransform , BICoreService {

	public Object get(ICubeTableService ti, int row);

	
}