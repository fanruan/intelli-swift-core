/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.api.ICubeTableService;
import com.fr.base.FRContext;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.data.AnalysisTableSource;
import com.fr.bi.etl.analysis.data.UserTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.file.XMLFileManager;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel
 *
 */
public class UserETLCubeManager extends XMLFileManager implements UserETLCubeManagerProvider {
	
	public static final String XML_TAG = "UserETLCubeManager";
	
	private Map<String, SingleUserETLTableCubeManager> threadMap = new ConcurrentHashMap<String, SingleUserETLTableCubeManager>();

	/**
	 * cube路径
	 */
	private Map<String, String> cubePathMap = new ConcurrentHashMap<String, String>();
	
	static {
		GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
            	UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
            	if(manager != null){
            		manager.envChanged();
            	}
            }
        });
	}
	

    public void invokeUpdate(String md5){
        synchronized (threadMap) {
            for (SingleUserETLTableCubeManager manager : threadMap.values()){
                if(manager.getSource() != null && manager.getSource().containsIDParentsWithMD5(md5)){
                    manager.addTask();
                }
            }
        }
    }

	@Override
	public ICubeTableService getTableIndex(BICore core, BIUser user){
		UserTableSource ut = getUserSource(core, user.getUserId());
		String md5Key = ut.fetchObjectCore().getID().getIdentityValue();
		SingleUserETLTableCubeManager manager = threadMap.get(md5Key);
		if(manager == null){
			synchronized (threadMap) {
				manager = threadMap.get(core.getIDValue());
				if(manager == null){
					manager = new SingleUserETLTableCubeManager(ut);
					threadMap.put(md5Key, manager);
				}
			}
		}
		return manager.getTableIndex();
	}
	
	public void releaseCurrentThread(){
		Iterator<Entry<String, SingleUserETLTableCubeManager>> iter = threadMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, SingleUserETLTableCubeManager> entry = iter.next();
			SingleUserETLTableCubeManager manager = entry.getValue();
			if(manager != null){
				manager.releaseCurrentThread();
			}
		}
		
	}
	
	private UserTableSource getUserSource(BICore core, long userId){
		AnalysisDataSourceManager ds = StableFactory.getMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, AnalysisDataSourceManager.class);
        AnalysisTableSource ts = null;
        try {
            ts = ds.getTableSourceByCore(core, new BIUser(userId));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return null;
        }
        return ts.createUserTableSource(userId);
	}
	
	
	public UserETLCubeManager(){
		synchronized (cubePathMap) {
			readXMLFile();
		}
	}
	
	@Override
	public void setCubePath(String md5Key, String path){
		synchronized (this) {
			cubePathMap.put(md5Key, path);
			try {
				FRContext.getCurrentEnv().writeResource(this);
			} catch (Exception e) {
				BILogger.getLogger().error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 */
	public void envChanged() {
		clear();
	}
	
	
	@Override
	public void clear(){
		synchronized (threadMap) {
			Iterator<Entry<String, SingleUserETLTableCubeManager>> iter = threadMap.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, SingleUserETLTableCubeManager> entry = iter.next();
				SingleUserETLTableCubeManager thread = entry.getValue();
				iter.remove();
				if(thread != null){
					thread.clear();
				}
			}
			threadMap.clear();
		}
		synchronized (cubePathMap) {
			cubePathMap.clear();
			readXMLFile();
		}
	}


	/* (non-Javadoc)
	 * @see com.fr.stable.file.XMLFileManagerProvider#fileName()
	 */
	@Override
	public String fileName() {
		return "useretlcubepath.xml";
	}


	/* (non-Javadoc)
	 * @see com.fr.stable.xml.XMLWriter#writeXML(com.fr.stable.xml.XMLPrintWriter)
	 */
	@Override
	public void writeXML(XMLPrintWriter writer) {
		// TODO Auto-generated method stub
		writer.startTAG(XML_TAG);
		Iterator<Entry<String, String>> iter = cubePathMap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			writer.startTAG(XML_TAG);
			writer.attr("key", entry.getKey());
			writer.attr("value", entry.getValue());
			writer.end();
		}
		writer.end();
	}


	/* (non-Javadoc)
	 * @see com.fr.stable.xml.XMLReadable#readXML(com.fr.stable.xml.XMLableReader)
	 */
	@Override
	public void readXML(XMLableReader reader) {
		// TODO Auto-generated method stub
		if(reader.isChildNode()){
			String key = reader.getAttrAsString("key", null);
			String value = reader.getAttrAsString("value", null);
			if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
				cubePathMap.put(key, value);
			}
		}
		
	}


	/**
	 * @param md5key
	 * @return
	 */
	@Override
	public String getCubePath(String md5key) {
		return cubePathMap.get(md5key);
	}

}