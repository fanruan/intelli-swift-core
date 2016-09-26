package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.arrange.BICubeBuildTopicManager;
import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.DateUtils;
import com.fr.json.JSONObject;

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
 * <p>
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
    private int retryNTimes;


    public BuildCubeTask(BIUser biUser, CubeBuild cubeBuild) {
        this.cubeBuild = cubeBuild;
        this.biUser = biUser;
        cubeConfiguration = cubeBuild.getCubeConfiguration();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        this.cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        uuid = "BUILD_CUBE" + UUID.randomUUID();
        retryNTimes = 1000;
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
        Long t = System.currentTimeMillis();
        BILogger.getLogger().info("start copy some files");
        cubeBuild.copyFileFromOldCubes();
        BILogger.getLogger().info("copy files cost time: " + DateUtils.timeCostFrom(t));
    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            String message = result.get();
            BILogger.getLogger().info(message);
            boolean cubeBuildSucceed = finishObserver.success();
            if (!cubeBuildSucceed) {
                checkTaskFinish();
            }

            if (cubeBuildSucceed) {
                cube.addVersion(System.currentTimeMillis());
                long start = System.currentTimeMillis();
                boolean replaceSuccess = replaceOldCubes();
                if (replaceSuccess) {
                    BICubeConfigureCenter.getTableRelationManager().finishGenerateCubes(biUser.getUserId(), cubeBuild.getTableRelationSet());
                    BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
                    BILogger.getLogger().info("Replace successful! Cost :" + DateUtils.timeCostFrom(start));
                } else {
                    message = "Cube replace failed ,the Cube files will not be replaced ";
                    BILogger.getLogger().error(message);
                }
            } else {
                message = "Cube build failed ,the Cube files will not be replaced ";
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), message, biUser.getUserId());
                BILogger.getLogger().error(message);
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            BILogger.getLogger().info("start persist data!");
            long t = System.currentTimeMillis();
            try {
                BICubeConfigureCenter.getPackageManager().finishGenerateCubes(biUser.getUserId());
                BILogger.getLogger().info("persist data finished! time cost: " + DateUtils.timeCostFrom(t));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    private void checkTaskFinish() {
        /**
         * Cube生成任务失败。但是Cube的局部可能还在继续生成。
         */
        while (!Thread.currentThread().isInterrupted()) {
            if (BIProcessorThreadManager.getInstance().isLeisure()) {
                return;
            } else {
                try {
                    Thread.sleep(5000);
                    BILogger.getLogger().info("Cube thread is busy currently.Monitor will check it again after 5s ");
                } catch (InterruptedException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }

        }
    }

    private boolean replaceOldCubes() {
        try {
            BILogger.getLogger().info("Start Replacing Old Cubes, Stop All Analysis");
            boolean replaceSuccess = false;
            for (int i = 0; i < retryNTimes; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
                for (ICubeResourceLocation location : BICubeDiskPrimitiveDiscovery.getInstance().getUnReleasedLocation()) {
                    BILogger.getLogger().error("error: the filePath is : "+location.getBaseLocation()+location.getChildLocation());
                }
                replaceSuccess = cubeBuild.replaceOldCubes();
                BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
                CubeReadingTableIndexLoader.getInstance(biUser.getUserId()).clear();
                if (!replaceSuccess) {
                    BILogger.getLogger().error("cube replace failed after " + i + " times try!It will try again in 5s");
                    Thread.sleep(5000);
                } else {
                    break;
                }
            }
            return replaceSuccess;
        } catch (Exception e) {
            String message = " cube build failed ! caused by \n" + e.getMessage();
            BILogger.getLogger().error(message, e);
            try{
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), message, biUser.getUserId());
            } catch (Exception e1) {
                BILogger.getLogger().error(e1.getMessage(), e1);
            }
            return false;
        }finally {
            BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
            CubeReadingTableIndexLoader.getInstance(biUser.getUserId()).clear();
        }
    }

    @Override
    public void run() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuild.getSources());
        operationManager.initialWatcher();
        operationManager.subscribeStartMessage();
        operationManager.setUpdateSettingSourceMap(cubeBuild.getUpdateSettingSources());
        operationManager.setConnectionMap(cubeBuild.getConnections());
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
