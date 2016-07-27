package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.arrange.BICubeBuildTopicManager;
import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.conf.CubeBuildByPart;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * This class created on 2016/4/19.
 *
 * @author Connery
 * @since 4.0
 * <p/>
 * edit by kary
 */
public class BuildCubeTask implements CubeTask {

    private CubeBuild cubeBuild;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    private BICubeFinishObserver<Future<String>> finishObserver;
    private String uuid;
//    private int updateTypes = DBConstant.SINGLE_TABLE_UPDATE.NEVER;


    public BuildCubeTask(BIUser biUser, CubeBuild cubeBuild) {
        this.cubeBuild = cubeBuild;
        this.biUser = biUser;
        cubeConfiguration = cubeBuild.getCubeConfiguration();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        this.cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        uuid = "BUILD_CUBE" + UUID.randomUUID();
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public CubeTaskType getTaskType() {
        if (cubeBuild.isSingleTable()) {
            return CubeTaskType.SINGLE;
        }
        return CubeTaskType.ALL;
    }

    @Override
    public void start() {
        BICubeConfigureCenter.getPackageManager().startBuildingCube(biUser.getUserId());
        if (cubeBuild instanceof CubeBuildByPart) {
            copyOldCubesToTempCubes();
        }
    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            BICubeConfigureCenter.getPackageManager().finishGenerateCubes(biUser.getUserId());
            if (!cubeBuild.isSingleTable()) {
                BICubeConfigureCenter.getTableRelationManager().finishGenerateCubes(biUser.getUserId(), cubeBuild.getTableRelationSet());
                BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
            }
            BILogger.getLogger().info(result.get());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            try {
                cube.addVersion(System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuild.getSources());
        operationManager.initialWatcher();
        operationManager.subscribeStartMessage();
        manager.registerDataSource(cubeBuild.getAllSingleSources());
        manager.registerRelation(cubeBuild.getTableSourceRelationSet());
        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuild.getBiTableSourceRelationPathSet());
        manager.registerTableRelationPath(relationPathSet);
        finishObserver = new BICubeFinishObserver<Future<String>>(new BIOperationID("FINEBI_E"));
        operationManager.setVersionMap(cubeBuild.getVersions());
        operationManager.generateDataSource(cubeBuild.getDependTableResource());
        operationManager.generateRelationBuilder(cubeBuild.getCubeGenerateRelationSet());
        operationManager.generateTableRelationPath(cubeBuild.getCubeGenerateRelationPathSet());
        IRouter router = BIFactoryHelper.getObject(IRouter.class);

        try {
            BIConfigureManagerCenter.getLogManager().relationPathSet(cubeBuild.getBiTableSourceRelationPathSet(), biUser.getUserId());
            BIConfigureManagerCenter.getLogManager().cubeTableSourceSet(cubeBuild.getAllSingleSources(), biUser.getUserId());
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

    private void copyOldCubesToTempCubes() {
        try {
            ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(Long.toString(biUser.getUserId()));
            ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
            if (new File(tempConf.getRootURI().getPath()).exists()) {
                BIFileUtils.delete(new File(tempConf.getRootURI().getPath()));
            }
            if (new File(advancedConf.getRootURI().getPath()).exists()) {
                BIFileUtils.copyFolder(new File(advancedConf.getRootURI().getPath()), new File(tempConf.getRootURI().getPath()));
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage());
        }

    }

    public static IMessage generateMessageDataSourceStart() {
        return buildTopic(new BIMessageTopic(BICubeBuildTopicTag.START_BUILD_CUBE));
    }

    private static IMessage buildTopic(IMessageTopic topic) {
        return new BIMessage(topic, null, null, null);
    }

    @Override
    public long getUserId() {
        return UserControl.getInstance().getSuperManagerID();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getUUID().equals(((BuildCubeTask) obj).getUUID());
    }

}
