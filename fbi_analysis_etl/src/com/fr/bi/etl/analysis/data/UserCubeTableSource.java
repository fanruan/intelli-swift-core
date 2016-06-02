package com.fr.bi.etl.analysis.data;

import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface UserCubeTableSource extends AnalysisCubeTableSource {

	/**
	 * @return
	 */
	long getUserId();

	/**
	 * @return
	 */
	Set<String> getSourceUsedMD5();

    boolean containsIDParentsWithMD5(String md5);
}