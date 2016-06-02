/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
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
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;
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


    private CubeBuildStuff cubeBuildStuff;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    @BIIgnoreField
    protected BICube cube;


    public UserETLUpdateTask(UserCubeTableSource source) {
        this.source = source;
        this.cubeBuildStuff = new UserETLCubeStuff(source,new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                try {
                    return URI.create(new BICubeLocation(BIPathUtils.createUserETLTableBasePath(UserETLUpdateTask.this.source.fetchObjectCore().getID().getIdentityValue()), path).getAbsolutePath());
                } catch (URISyntaxException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }
        },  source.getUserId() );
        this.biUser = new BIUser(source.getUserId());
        cubeConfiguration = cubeBuildStuff.getCubeConfiguration();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        this.cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    @Override
    public void run() {
        final ICubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(this.source));
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



	private static TableCubeFile getOldCube(String md5){
		UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
		String path = manager.getCubePath(md5);
		return new TableCubeFile(BIPathUtils.createUserETLCubePath(md5, path));
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
	public boolean check() {
        return false;
//		TableCubeFile cube = getOldCube(source.fetchObjectCore().getID().getIdentityValue());
//		return cube.checkCubeVersion() && checkSourceVersion();
	}
	
	/**
	 * @return
	 */
	private boolean checkSourceVersion() {
        return false;
//		return getTableVersion() == getOldCube(source.fetchObjectCore().getID().getIdentityValue()).getTableVersion();
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
}
