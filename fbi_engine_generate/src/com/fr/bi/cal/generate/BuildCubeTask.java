package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.arrange.BICubeBuildTopicManager;
import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.engine.CubeBuildStuffManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BITableRelationConfigurationProvider;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * This class created on 2016/4/19.
 *
 * @author Connery
 * @since 4.0
 *
 * edit by wuk
 * 增加单表更新功能
 */
public class BuildCubeTask implements CubeTask {

    private CubeBuildStuffManager cubeBuildStuffManager;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    private BICubeFinishObserver<Future<String>> finishObserver;
    /*单表更新*/
    private BITable biTable;

    public BuildCubeTask(BIUser biUser) {

        this.biUser = biUser;
        cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));

//        cubeBuildStuffManager = new CubeBuildStuffManager(biUser);
//        cubeBuildStuffManager.initialCubeStuff();
    }
    /*单表更新*/
    public BuildCubeTask(BIUser biUser, BITable biTable) {
        this.biUser = biUser;
        cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        this.biTable=biTable;

    }


    public void setCubeBuildStuffManager(CubeBuildStuffManager cubeBuildStuffManager) {
        this.cubeBuildStuffManager = cubeBuildStuffManager;
    }

    @Override
    public String getUUID() {
        return "BUILD_CUBE";
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.BUILD;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            BILogger.getLogger().info(result.get());
//            BIFileUtils.moveFile(BICubeConfiguration.getTempConf(Long.toString(biUser.getUserId())).getRootURI().getPath(),
//                    BICubeConfiguration.getConf(Long.toString(biUser.getUserId())).getRootURI().getPath());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void run() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuildStuffManager.getSources());
        operationManager.initialWatcher();
            /*单表更新*/
        if(null!=biTable){
        Set<ITableSource> tableSourceSet = new HashSet<ITableSource>();
        for (ITableSource iTableSource : cubeBuildStuffManager.getAllSingleSources()) {
            if(iTableSource.equals(BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(biTable.getID(),biUser))){
                tableSourceSet.add(iTableSource);
                cubeBuildStuffManager.setAllSingleSources(tableSourceSet);
                Set<List<Set<ITableSource>>> calculateTableSource = cubeBuildStuffManager.calculateTableSource(tableSourceSet);
                cubeBuildStuffManager.setDependTableResource(calculateTableSource);
                BITableRelationConfigurationProvider tableRelationManager = BIConfigureManagerCenter.getTableRelationManager();

                Set<BITableRelation> allTableRelation = tableRelationManager.getAllTableRelation(biUser.getUserId());
                Set<BITableRelation> tableRelationSet = new HashSet<BITableRelation>();
                for (BITableRelation biTableRelation : allTableRelation) {
                    if (biTableRelation.getPrimaryTable().getID().equals(biTable.getID())) {
                        tableRelationSet.add(biTableRelation);
                    }
                }
                cubeBuildStuffManager.setTableRelationSet(tableRelationSet);


                try {
                    Set<BITableRelationPath> allTablePath = BIConfigureManagerCenter.getTableRelationManager().getAllTablePath(biUser.getUserId());
                    Set<BITableRelationPath> tablePath = new HashSet<BITableRelationPath>();
                    for (BITableRelationPath biTableRelationPath : allTablePath) {
                        if(biTableRelationPath.getFirstRelation().getPrimaryTable().equals(biTable.getID())){
                            tablePath.add(biTableRelationPath);
                        }
                    }
                    cubeBuildStuffManager.setRelationPaths(cubeBuildStuffManager.convertPaths(tablePath));
                } catch (BITableRelationConfusionException e) {
                    e.printStackTrace();
                } catch (BITablePathConfusionException e) {
                    e.printStackTrace();
                } catch (BITablePathEmptyException e) {
                    e.printStackTrace();
                }finally {

                }

                break;
            }
        }


        }

        manager.registerDataSource(cubeBuildStuffManager.getAllSingleSources());
        manager.registerRelation(cubeBuildStuffManager.getTableSourceRelationSet());
        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuildStuffManager.getRelationPaths());
        manager.registerTableRelationPath(relationPathSet);
        finishObserver = new BICubeFinishObserver<Future<String>>(new BIOperationID("FINEBI_E"));
        operationManager.generateDataSource(cubeBuildStuffManager.getDependTableResource());
        operationManager.generateRelationBuilder(cubeBuildStuffManager.getTableSourceRelationSet());
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

    public static IMessage generateMessageDataSourceStart() {
        return buildTopic(new BIMessageTopic(BICubeBuildTopicTag.START_BUILD_CUBE));

    }

    private static IMessage buildTopic(IMessageTopic topic) {
        return new BIMessage(topic, null, null, null);
    }

    @Override
    public long getUserId() {
        return -999;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }
}
