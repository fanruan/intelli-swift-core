/**
 * 
 */
package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.sql.analysis.data.AnalysisSQLDataSource;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.bi.sql.analysis.manager.UserSQLDataManagerProvider;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.program.BIConstructorUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel
 *
 */
public class UserSQLCubeTILoader implements ICubeDataLoader {

    public static Map<Long, UserSQLCubeTILoader> userMap = new ConcurrentHashMap<Long, UserSQLCubeTILoader>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 3766201942015813978L;

    private BIUser user;

	/**
	 * @param userId
	 */
	public UserSQLCubeTILoader(long userId) {
        this.user = new BIUser(userId);
	}

    public static UserSQLCubeTILoader getInstance(long userId) {
        return BIConstructorUtils.constructObject(userId, UserSQLCubeTILoader.class, userMap, false);
    }

    private UserSQLDataManagerProvider getCubeManager(){
        return BIAnalysisSQLManagerCenter.getUserETLCubeManagerProvider();
    }


    @Override
    public long getUserId() {
        return user.getUserId();
    }

    @Override
    public boolean needReadTempValue() {
        return false;
    }

    @Override
    public boolean needReadCurrentValue() {
        return false;
    }

    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        return null;
    }

    @Override
	public void releaseCurrentThread(){
        UserSQLDataManagerProvider manager = BIAnalysisSQLManagerCenter.getUserETLCubeManagerProvider();
        if(manager != null){
            manager.releaseCurrentThread();
        }
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource, int start, int end) {
        return getTableIndex(tableSource);
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return getCubeManager().getTableIndex((AnalysisSQLDataSource)tableSource, user);
    }

    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return new IndexKey(column.getFieldName());
    }

    @Override
	public void clear() {
        synchronized (this) {
            if(userMap != null){
                userMap.clear();
            }
        }
	}

    @Override
    public long getVersion() {
        return 0;
    }
}