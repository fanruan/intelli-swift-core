/**
 * 
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeBuildStuffManagerTableSource;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.arrange.BICubeBuildTopicManager;
import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
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

import static com.fr.bi.cal.generate.BuildCubeTask.generateMessageDataSourceStart;

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
        this.cubeBuildStuff = new CubeBuildStuffManagerTableSource(source,new ICubeConfiguration() {
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

        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();


        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuildStuff.getSources());
        operationManager.initialWatcher();

        manager.registerDataSource(cubeBuildStuff.getAllSingleSources());
        manager.registerRelation(cubeBuildStuff.getTableSourceRelationSet());
        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuildStuff.getRelationPaths());
        manager.registerTableRelationPath(relationPathSet);
        operationManager.generateDataSource(cubeBuildStuff.getDependTableResource());
        operationManager.generateRelationBuilder(cubeBuildStuff.getTableSourceRelationSet());
        operationManager.generateTableRelationPath(relationPathSet);
        IRouter router = BIFactoryHelper.getObject(IRouter.class);
        try {
            router.deliverMessage(generateMessageDataSourceStart());
        } catch (BIDeliverFailureException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private Set<BITableSourceRelationPath> filterPath(Set<BITableSourceRelationPath> paths) {
        Iterator<BITableSourceRelationPath> iterator = paths.iterator();
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        while (iterator.hasNext()) {
            BITableSourceRelationPath path = iterator.next();
            if (path.getAllRelations().size() > 1) {
                result.add(path);
            }
        }
        return result;
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
}
