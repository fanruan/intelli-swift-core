/**
 * 
 */
package com.fr.bi.etl.analysis.tableobj;

import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.Delete;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.engine.index.NullTableIndexException;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.io.File;

/**
 * @author Daniel
 *
 */
public class ETLTableObject implements Release, Delete {

	private String path;
	
	private ICubeTableService ti;
	
	private SingleUserNIOReadManager manager = new SingleUserNIOReadManager(-1);
	
	private volatile boolean isClear = false;
	
	public ETLTableObject(String md5, String path){
		this.path = BIPathUtils.createUserETLTablePath(md5, path);
		ti = new BITableIndex(this.path, manager);
	}
	
	
	public ICubeTableService getTableIndex(){
		if(isClear){
			throw new NullTableIndexException();
		}
		return ti;
	}
	

	/* (non-Javadoc)
	 * @see com.fr.bi.common.inter.Release#clear()
	 */
	@Override
	public void clear() {
		isClear = true;
		ti.clear();
		manager.clear();
	}

	/**
	 * 
	 */
	@Override
	public void delete() {
		BIFileUtils.delete(new File(this.path));
	}
	
}