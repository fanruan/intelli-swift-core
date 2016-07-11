/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.etl.analysis.tableobj.ETLTableObject;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.structure.queue.QueueThread;
import com.fr.bi.stable.structure.queue.ThreadUnitedQueue;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.stable.StringUtils;

import java.io.File;

/**
 * @author Daniel
 *
 */
public class SingleUserETLTableCubeManager implements Release {
	
	private QueueThread<UserETLUpdateTask> updateTask;
	
	private ThreadUnitedQueue<ETLTableObject> tq = new ThreadUnitedQueue<ETLTableObject>();

    private UserCubeTableSource source;

    public UserCubeTableSource getSource() {
        return source;
    }

	private String getSavedPath(){
        UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
    	if(manager != null){
    		return manager.getCubePath(source.fetchObjectCore().getID().getIdentityValue());
    	}
    	return null;
	}
	
	public ICubeTableService getTableIndex(){
		return tq.get().getTableIndex();
	}
	
	public void releaseCurrentThread(){
		tq.releaseObject();
	}
	
	public SingleUserETLTableCubeManager (UserCubeTableSource source){
		this.source = source;
		String path = getSavedPath();
		if(path != null && new File(BIPathUtils.createUserETLCubePath(source.fetchObjectCore().getIDValue(), path)).exists()){
			tq.add(new ETLTableObject(source, path));
		}
		addTask();
	}
	
	public void addTask(){
		if(updateTask == null){
			synchronized (this) {
				if(updateTask == null){
					updateTask = new QueueThread<UserETLUpdateTask>();
					updateTask.setCheck(new BrokenTraversal<UserETLUpdateTask>() {
						@Override
						public boolean actionPerformed(UserETLUpdateTask data) {
							return checkCubePath();
						}
					});
					updateTask.setTraversal(new Traversal<UserETLUpdateTask>() {
						
						@Override
						public void actionPerformed(UserETLUpdateTask data) {
							try {
								long version = 0;
								if (!tq.isEmpty()){
									version = tq.get().getTableIndex().getTableVersion(new IndexKey(StringUtils.EMPTY));
								}
								if(data.check(version)){
									return;
								}
								data.start();
								data.run();
								data.end();
								tq.add(new ETLTableObject(source, data.getPath()));
							} catch (Exception e){
								BILogger.getLogger().error(e.getMessage(), e);
							} finally {
							}
						}
					});
                    updateTask.start();
				}
			}
		}
		if(updateTask.size() < 2) {
			updateTask.add(new UserETLUpdateTask(source));
		}
	}
	
	
    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIPathUtils.createUserETLTableBasePath(source.fetchObjectCore().getID().getIdentityValue())));
    }
	
	protected boolean checkVersion(){
        if (tq.isEmpty()){
            return false;
        } else {
            long version = tq.get().getTableIndex().getTableVersion(new IndexKey(StringUtils.EMPTY));
            return new UserETLUpdateTask(source).check(version);
        }
    }

	/**
	 * 
	 */
	@Override
	public void clear() {
		if(updateTask != null){
			updateTask.clear();
			updateTask = null;
		}
		tq.clear();
	}
	
}