/**
 * 
 */
package com.fr.bi.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fr.bi.etl.analysis.manager.UserETLCubeManager;
import com.fr.bi.etl.analysis.manager.UserETLCubeManagerProvider;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.engine.index.AbstractTIPathLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.stable.bridge.StableFactory;

/**
 * @author Daniel
 *
 */
public class UserETLCubeTILoader extends AbstractTIPathLoader {

    public static Map<Long, AbstractTIPathLoader> userMap = new ConcurrentHashMap<Long, AbstractTIPathLoader>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 3766201942015813978L;

	/**
	 * @param userId
	 */
	public UserETLCubeTILoader(long userId) {
		super(userId);
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.stable.engine.index.CubeTIPathLoader#getTableIndexByPath(java.lang.String)
	 */
	@Override
	public ICubeTableService getTableIndexByPath(SourceFile file) {
		UserETLCubeManagerProvider manager = StableFactory.getMarkedObject(UserETLCubeManagerProvider.class.getName(), UserETLCubeManagerProvider.class);
		return manager.getTableIndex(file.getPath(), user);
	}

    @Override
	public void releaseCurrentThread(){
        UserETLCubeManager manager = StableFactory.getMarkedObject(UserETLCubeManager.class.getName(), UserETLCubeManager.class);
        if(manager != null){
            manager.releaseCurrentThread();
        }
    }

	@Override
	public void clear() {

	}
}