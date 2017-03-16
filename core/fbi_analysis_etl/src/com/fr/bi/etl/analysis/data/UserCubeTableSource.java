package com.fr.bi.etl.analysis.data;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface UserCubeTableSource extends AnalysisCubeTableSource {

	/**
	 * @return
	 */
	long getUserId();

    boolean containsIDParentsWithMD5(String md5, long userId);

    AnalysisCubeTableSource getAnalysisCubeTableSource();

	boolean isParentAvailable();


	Set<CubeTableSource> getParentSource();

}