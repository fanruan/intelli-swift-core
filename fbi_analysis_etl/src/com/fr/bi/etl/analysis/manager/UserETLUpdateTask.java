/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.etl.analysis.data.UserTableSource;
import com.fr.bi.etl.analysis.tableobj.UserETLIndexGenerator;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.json.JSONObject;
import com.fr.stable.core.UUID;

import java.util.*;

/**
 * @author Daniel
 *
 */
public class UserETLUpdateTask implements CubeTask {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8163408533936978327L;
	private String path = UUID.randomUUID().toString();
	
	private UserTableSource source;
	
	private UserETLIndexGenerator generator;
	
	private Date start;
	private Date end;
	
	/**
	 * @param source
	 */
	public UserETLUpdateTask(UserTableSource source) {
		this.source = source;
		generator = new UserETLIndexGenerator(source, getTableVersion(), path);
	}
	

	private static TableCubeFile getOldCube(String md5){
		UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
		String path = manager.getCubePath(md5);
		return new TableCubeFile(BIPathUtils.createUserETLTablePath(md5, path));
	}

	public String getPath(){
		return path;
	}
	
	
	@Override
	public void start() {
		start = new Date();
		BILogger.getLogger().error("started in file path:" + path);
	}
	
	
	@Override
	public void run() {
		generator.generateCube();
	}

	
	@Override
	public void end() {
		UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
		manager.setCubePath(source.fetchObjectCore().getID().getIdentityValue(), getPath());
		end = new Date();
		manager.invokeUpdate(source.fetchObjectCore().getID().getIdentityValue());
		manager.releaseCurrentThread();
	}


	/* (non-Javadoc)
	 * @see com.fr.json.JSONCreator#createJSON()
	 */
	@Override
	public JSONObject createJSON() throws Exception {
		JSONObject jo = new JSONObject();
		if(start != null){
			jo.put("start", start.getTime());
		}
		if(end != null){
			jo.put("end", end.getTime());
		}
		jo.put("percent", getPercent());
		jo.put("name", source.toString());
		return jo;
	}


	/* (non-Javadoc)
	 * @see com.fr.bi.stable.engine.CubeTask#getUUID()
	 */
	@Override
	public String getUUID() {
		return getPath();
	}


	/* (non-Javadoc)
	 * @see com.fr.bi.stable.engine.CubeTask#getTaskType()
	 */
	@Override
	public CubeTaskType getTaskType() {
		return CubeTaskType.CHECK;
	}


	/* (non-Javadoc)
	 * @see com.fr.bi.stable.engine.CubeTask#getUserId()
	 */
	@Override
	public long getUserId() {
		return source.getUserId();
	}


	/**
	 * @return
	 */
	public boolean check() {
		TableCubeFile cube = getOldCube(source.fetchObjectCore().getID().getIdentityValue());
		return cube.checkCubeVersion() && checkSourceVersion();
	}
	
	/**
	 * @return
	 */
	private boolean checkSourceVersion() {
		return getTableVersion() == getOldCube(source.fetchObjectCore().getID().getIdentityValue()).getTableVersion();
	}
	
	private int getTableVersion(){
		Set<String> set = source.getSourceUsedMD5();
		TreeSet<String> ts = new TreeSet<String>();
		ts.addAll(set);
		IntList versionList = new IntList();
		Iterator<String> iter = ts.iterator();
		while(iter.hasNext()){
			versionList.add(getOldCube(iter.next()).getTableVersion());
		}
		return Arrays.hashCode(versionList.toArray());
	}


	public int getPercent(){
		if(generator == null){
			return 0;
		}
		return generator.getPercent();
	}
}