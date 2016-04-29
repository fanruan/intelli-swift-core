/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.finebi.cube.api.ICubeTableService;

/**
 * @author Daniel
 *
 */
public interface UserETLCubeManagerProvider extends Release{
	
	public void setCubePath(String md5Key, String path);

	/**
	 * @param md5
	 * @param userId
	 * @return
	 */
	public ICubeTableService getTableIndex(String md5, BIUser user);

	/**
	 * @param md5
	 * @return
	 */
	public String getCubePath(String md5);

}