package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface UserCubeTableSource extends CubeTableSource {

	/**
	 * @return
	 */
	long getUserId();

	/**
	 * @return
	 */
	Set<String> getSourceUsedMD5();

    boolean containsIDParentsWithMD5(String md5);

    void setLoader(ICubeDataLoader loader);
}