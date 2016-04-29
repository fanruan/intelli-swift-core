/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import java.io.File;

import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.data.UserTableSource;
import com.fr.bi.etl.analysis.tableobj.ETLTableObject;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.structure.queue.QueueThread;
import com.fr.bi.stable.structure.queue.ThreadUnitedQueue;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.stable.bridge.StableFactory;

/**
 * @author Daniel
 *
 */
public class SingleUserETLTableCubeManager implements Release {
	
	private QueueThread<UserETLUpdateTask> updateTask;
	
	private ThreadUnitedQueue<ETLTableObject> tq = new ThreadUnitedQueue<ETLTableObject>();

    private UserTableSource source;

    public UserTableSource getSource() {
        return source;
    }

	private String getSavedPath(){
		UserETLCubeManager manager = StableFactory.getMarkedObject(UserETLCubeManager.class.getName(), UserETLCubeManager.class);
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
	
	public SingleUserETLTableCubeManager (UserTableSource source){
		this.source = source;
		String path = getSavedPath();
		if(path != null){
			tq.add(new ETLTableObject(source.fetchObjectCore().getID().getIdentityValue(), path));
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
								if(data.check()){
									return;
								}
								data.start();
								data.run();
								data.end();
								tq.add(new ETLTableObject(source.fetchObjectCore().getID().getIdentityValue(), data.getPath()));
							} catch (Exception e){
								BILogger.getLogger().error(e.getMessage());
							} finally {
							}
						}
					});
				}
			}
		}
		updateTask.add(new UserETLUpdateTask(source));
	}
	
	
    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIPathUtils.createUserETLTableBasePath(source.fetchObjectCore().getID().getIdentityValue())));
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