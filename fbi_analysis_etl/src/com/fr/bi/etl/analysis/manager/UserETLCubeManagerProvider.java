/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.finebi.cube.api.ICubeTableService;

/**
 * @author Daniel
 *
 */
public interface UserETLCubeManagerProvider extends Release{
	
	void setCubePath(String md5Key, String path);

	/**
	 * @return
	 */
	ICubeTableService getTableIndex(BICore core, BIUser user);

	/**
	 * @param md5
	 * @return
	 */
	String getCubePath(String md5);

    void envChanged();

    void releaseCurrentThread();

    void invokeUpdate(String identityValue);
}