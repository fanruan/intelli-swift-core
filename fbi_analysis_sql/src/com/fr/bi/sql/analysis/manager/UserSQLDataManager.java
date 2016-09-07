/**
 * 
 */
package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.sql.analysis.data.AnalysisSQLDataSource;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

/**
 * @author Daniel
 *
 */
public class UserSQLDataManager implements UserSQLDataManagerProvider {
	
	static {
		GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                UserSQLDataManagerProvider manager = BIAnalysisSQLManagerCenter.getUserETLCubeManagerProvider();
            	if(manager != null){
            		manager.envChanged();
            	}
            }
        });
	}
	
	@Override
	public ICubeTableService getTableIndex(AnalysisSQLDataSource source, BIUser user){
		return null;
	}

    @Override
    public void releaseCurrentThread() {

    }

    /**
	 * 
	 */
	public void envChanged() {
		clear();
	}

    @Override
    public void clear() {

    }
}