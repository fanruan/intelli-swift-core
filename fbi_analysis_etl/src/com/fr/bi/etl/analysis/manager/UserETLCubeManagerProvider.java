/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;

/**
 * @author Daniel
 *
 */
public interface UserETLCubeManagerProvider extends Release{
	
	void setCubePath(String md5Key, String path);

	/**
	 * @return
	 */
	ICubeTableService getTableIndex(AnalysisCubeTableSource core, BIUser user);

	/**
	 * @param md5
	 * @return
	 */
	String getCubePath(String md5);

    boolean isCubeGenerating(String md5);

    void envChanged();

    void releaseCurrentThread();

    void invokeUpdate(String identityValue);
}