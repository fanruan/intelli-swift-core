package com.fr.bi.etl.analysis.data;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface UserCubeTableSource extends AnalysisCubeTableSource {

	/**
	 * @return
	 */
	long getUserId();

    boolean containsIDParentsWithMD5(String md5);
}