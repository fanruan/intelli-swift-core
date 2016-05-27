package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.build.CubeBuildStuffManager;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.concurrent.Future;

/**
 * Created by wuk on 16/5/24.
 * //todo
 * 单表更新
 */
public class BuildCubeTaskIncremental implements CubeTask {

    private CubeBuildStuffManager cubeBuildStuffManager;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    private BICubeFinishObserver<Future<String>> finishObserver;
    /*单表更新*/
    private BITable biTable;

    public BuildCubeTaskIncremental(BIUser biUser, BITable biTable) {
        this.biUser = biUser;
        cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        this.biTable = biTable;

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
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void run() {
//        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
//        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuildStuffManager.getSources());
//        operationManager.initialWatcher();
//        if (null == biTable) {
//            return;
//        }
//
//        Set<CubeTableSource> tableSourceSet = new HashSet<CubeTableSource>();
//        try {
//            tableSourceSet.add(BICubeConfigureCenter.getDataSourceManager().getTableSource(biTable.getID()));
//        } catch (BIKeyAbsentException e) {
//            e.printStackTrace();
//        }
//        cubeBuildStuffManager.setAllSingleSources(tableSourceSet);
//        cubeBuildStuffManager.setDependTableResource(cubeBuildStuffManager.calculateTableSource(tableSourceSet));
//        BITableRelationConfigurationProvider tableRelationManager = BIConfigureManagerCenter.getTableRelationManager();
//        Table table = new BITable(biTable);
//        try {
//            Set<BITableRelation> tableRelationSet = new HashSet<BITableRelation>();
//            if (tableRelationManager.containTablePrimaryRelation(biUser.getUserId(), biTable)) {
//                IRelationContainer primaryRelation = tableRelationManager.getPrimaryRelation(biUser.getUserId(), table);
//                tableRelationSet.addAll(primaryRelation.getContainer());
//            }
//            if (tableRelationManager.containTableForeignRelation(biUser.getUserId(), biTable)) {
//                IRelationContainer foreignRelation = tableRelationManager.getForeignRelation(biUser.getUserId(), table);
//                tableRelationSet.addAll(foreignRelation.getContainer());
//            }
//            cubeBuildStuffManager.setTableRelationSet(tableRelationSet);
//
//            Set<BITableRelationPath> allTablePath = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(biUser.getUserId());
//            Set<BITableRelationPath> tablePath = new HashSet<BITableRelationPath>();
//            for (BITableRelationPath biTableRelationPath : allTablePath) {
//                for (BITableRelation biTableRelation : tableRelationSet) {
//                    if (biTableRelationPath.getAllRelations().contains(biTableRelation)) {
//                        tablePath.add(biTableRelationPath);
//                        break;
//                    }
//                }
//            }
//            cubeBuildStuffManager.setRelationPaths(cubeBuildStuffManager.convertPaths(tablePath));
//
//        } catch (BITableAbsentException e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        } catch (BITablePathConfusionException e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        } catch (BITableRelationConfusionException e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        }
//
//
//        manager.registerDataSource(cubeBuildStuffManager.getAllSingleSources());
//        manager.registerRelation(cubeBuildStuffManager.getTableSourceRelationSet());
//        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuildStuffManager.getRelationPaths());
//        manager.registerTableRelationPath(relationPathSet);
//        finishObserver = new BICubeFinishObserver<Future<String>>(new BIOperationID("FINEBI_E"));
//        operationManager.generateDataSource(cubeBuildStuffManager.getDependTableResource());
//        operationManager.generateRelationBuilder(cubeBuildStuffManager.getTableSourceRelationSet());
//        operationManager.generateTableRelationPath(relationPathSet);
//        IRouter router = BIFactoryHelper.getObject(IRouter.class);
//        try {
//            router.deliverMessage(generateMessageDataSourceStart());
//        } catch (BIDeliverFailureException e) {
//            throw BINonValueUtils.beyondControl(e);
//        }

    }

//    private Set<BITableSourceRelationPath> filterPath(Set<BITableSourceRelationPath> paths) {
//        Iterator<BITableSourceRelationPath> iterator = paths.iterator();
//        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
//        while (iterator.hasNext()) {
//            BITableSourceRelationPath path = iterator.next();
//            if (path.getAllRelations().size() > 1) {
//                result.add(path);
//            }
//        }
//        return result;
//    }

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
