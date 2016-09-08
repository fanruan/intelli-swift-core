/**
 * 
 */
package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.fr.bi.sql.analysis.data.AnalysisSQLDataSource;

/**
 * @author Daniel
 *
 */
public interface UserSQLDataManagerProvider extends Release{
	
	ICubeTableService getTableIndex(AnalysisSQLDataSource core, BIUser user);

    void releaseCurrentThread();

    void envChanged();
}