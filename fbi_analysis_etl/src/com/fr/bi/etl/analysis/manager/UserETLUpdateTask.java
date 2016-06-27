/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.module.UserETLCubeTILoader;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.structure.collection.list.LongList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.core.UUID;

import java.net.URI;
import java.net.URISyntaxException;
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
	
	private UserCubeTableSource source;

	private Date start;
	private Date end;

    private BIUser biUser;
    @BIIgnoreField
    protected BICube cube;


    public UserETLUpdateTask(UserCubeTableSource source) {
        this.source = source;
        this.biUser = new BIUser(source.getUserId());
        this.cube = new BICube(new BICubeResourceRetrieval(new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                try {
                    return URI.create(new BICubeLocation(BIPathUtils.createUserETLTableBasePath(UserETLUpdateTask.this.source.fetchObjectCore().getID().getIdentityValue()), path).getAbsolutePath());
                } catch (URISyntaxException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }
        }), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    @Override
    public void run() {
        final CubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(this.source));
        ICubeFieldSource[] columns = this.source.getFieldsArray(new HashSet<CubeTableSource>());
        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
        for (ICubeFieldSource col : columns) {
            columnList.add(new BICubeFieldSource(this.source, col.getFieldName(), col.getClassType(), col.getFieldSize()));
        }
        tableEntityService.recordTableStructure(columnList);
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            cubeFieldSources[i] = fieldList.get(i);
        }
        tableEntityService.recordRowCount(this.source.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    e.printStackTrace();
                }
            }
        }, cubeFieldSources, UserETLCubeTILoader.getInstance(biUser.getUserId())));
        tableEntityService.addVersion(getTableVersion());
        ICubeFieldSource[] fields = source.getFieldsArray(new HashSet<CubeTableSource>());
        for (int i = 0; i < fields.length; i++) {
            ICubeFieldSource field = fields[i];
            Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
            while (columnKeyIterator.hasNext()) {
                BIColumnKey targetColumnKey = columnKeyIterator.next();
                new BIFieldIndexGenerator(cube, source, field, targetColumnKey).mainTask(null);
            }
        }
    }



	private  long getBaseSourceVersion(CubeTableSource source){
        return BICubeManager.getInstance().fetchCubeLoader(biUser.getUserId()).getTableIndex(source).getTableVersion(new IndexKey(StringUtils.EMPTY));
	}

	public String getPath(){
		return path;
	}
	
	
	@Override
	public void start() {
		start = new Date();
		BILogger.getLogger().info("started in file path:" + path);
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
	public boolean check(long oldVersion) {
        UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
        return manager.getCubePath(source.fetchObjectCore().getID().getIdentityValue()) != null && checkSourceVersion(oldVersion);
	}
	
	/**
	 * @return
	 */
	private boolean checkSourceVersion(long oldVersion) {
		return oldVersion == getTableVersion();
	}
	
	private long getTableVersion(){

		TreeMap<String, CubeTableSource> tm = new TreeMap<String, CubeTableSource>();
		Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        for (CubeTableSource s : source.getSourceUsedBaseSource(set, new HashSet<CubeTableSource>())){
            tm.put(s.fetchObjectCore().getIDValue(), s);
        }
		LongList versionList = new LongList();
		Iterator<CubeTableSource> iter = tm.values().iterator();
		while(iter.hasNext()){
			versionList.add(getBaseSourceVersion(iter.next()));
		}
		return Arrays.hashCode(versionList.toArray());
	}
}
