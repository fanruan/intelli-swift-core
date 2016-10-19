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
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BICubeRelationUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.DateUtils;
import com.fr.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
    private static final Logger logger = LoggerFactory.getLogger(BuildCubeTask.class);
    private CubeBuild cubeBuild;
    protected BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    protected BICubeFinishObserver<Future<String>> finishObserver;
    private int retryNTimes;


    public BuildCubeTask(BIUser biUser, CubeBuild cubeBuild) {
        this.cubeBuild = cubeBuild;
        this.biUser = biUser;
        cubeConfiguration = cubeBuild.getCubeConfiguration();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        this.cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        retryNTimes = 1000;
    }

    @Override
    public String getTaskId() {
        return cubeBuild.getCubeTaskId();
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
        BIConfigureManagerCenter.getLogManager().logStart(biUser.getUserId());
        BICubeConfigureCenter.getPackageManager().startBuildingCube(biUser.getUserId());
        Long t = System.currentTimeMillis();
        BILoggerFactory.getLogger().info("start copy some files");
        cubeBuild.copyFileFromOldCubes();
        BILoggerFactory.getLogger().info("copy files cost time: " + DateUtils.timeCostFrom(t));
    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            String message = result.get();
            BILoggerFactory.getLogger().info(message);
            boolean cubeBuildSucceed = finishObserver.success();
            if (!cubeBuildSucceed) {
                checkTaskFinish();
            }

            if (cubeBuildSucceed) {
                cube.addVersion(System.currentTimeMillis());
                long start = System.currentTimeMillis();
                boolean replaceSuccess = replaceOldCubes();
                if (replaceSuccess) {
                    BICubeConfigureCenter.getTableRelationManager().finishGenerateCubes(biUser.getUserId());
                    BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
                    BILoggerFactory.getLogger().info("Replace successful! Cost :" + DateUtils.timeCostFrom(start));
                } else {
                    message = "Cube replace failed ,the Cube files will not be replaced ";
                    BILoggerFactory.getLogger().error(message);
                }
            } else {
                message = "Cube build failed ,the Cube files will not be replaced ";
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), message, biUser.getUserId());
                BILoggerFactory.getLogger().error(message);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {

        }
    }

    protected void checkTaskFinish() {
        /**
         * Cube生成任务失败。但是Cube的局部可能还在继续生成。
         */
        while (!Thread.currentThread().isInterrupted()) {
            if (BIProcessorThreadManager.getInstance().isLeisure()) {
                return;
            } else {
                try {
                    Thread.sleep(5000);
                    BILoggerFactory.getLogger().info("Cube thread is busy currently.Monitor will check it again after 5s ");
                } catch (InterruptedException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            }

        }
    }

    private boolean replaceOldCubes() {
        try {
            logger.info("Start Replacing Old Cubes, Stop All Analysis");
            boolean replaceSuccess = false;
            for (int i = 0; i < retryNTimes; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
                for (ICubeResourceLocation location : BICubeDiskPrimitiveDiscovery.getInstance().getUnReleasedLocation()) {
                    BILoggerFactory.getLogger().error("error: the filePath is : " + location.getBaseLocation() + location.getChildLocation());
                }
                replaceSuccess = cubeBuild.replaceOldCubes();
                BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
                CubeReadingTableIndexLoader.getInstance(biUser.getUserId()).clear();
                if (!replaceSuccess) {
                    logger.error("cube replace failed after " + i + " times try!It will try again in 5s");
                    Thread.sleep(5000);
                } else {
                    break;
                }
            }
            return replaceSuccess;
        } catch (Exception e) {
            String message = " cube build failed ! caused by \n" + e.getMessage();
            logger.error(message, e);
            try {
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), message, biUser.getUserId());
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
            return false;
        } finally {
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
        Map<CubeTableSource, UpdateSettingSource> updateSettingSources = cubeBuild.getUpdateSettingSources();
        operationManager.setUpdateSettingSourceMap(updateSettingSources);
        operationManager.setConnectionMap(cubeBuild.getConnections());
        manager.registerDataSource(cubeBuild.getAllSingleSources());
        logTable(cubeBuild.getAllSingleSources(), updateSettingSources);
        manager.registerRelation(cubeBuild.getTableSourceRelationSet());
        logRelation(cubeBuild.getTableSourceRelationSet());
        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuild.getBiTableSourceRelationPathSet());
        manager.registerTableRelationPath(relationPathSet);
        logPath(relationPathSet);
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

    private void logTable(Set<CubeTableSource> tableSourceSet, Map<CubeTableSource, UpdateSettingSource> updateSettingSources) {
        Map<Integer, String> updateTypeMap = new HashMap();
        updateTypeMap.put(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL, "All");
        updateTypeMap.put(DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART, "Part");
        updateTypeMap.put(DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER, "Never");
        logger.info("***************Table*****************");
        Integer tableCount = 0;
        if (tableSourceSet != null) {
            for (CubeTableSource tableSource : tableSourceSet) {
                int updateType = null == updateSettingSources.get(tableSource) ? DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL : updateSettingSources.get(tableSource).getUpdateType();
                logger.info(BIStringUtils.append(
                        "\n" + "       table: " + (tableCount++),
                        "\n" + "       Table Name:", tableSource.getTableName(),
                        "\n" + "       Table ID:", tableSource.getSourceID(),
                        "\n" + "       update type:", updateTypeMap.get(updateType)
                ));
            }
        }
        logger.info("***************Table end*****************\n");

    }

    private void logRelation(Set<BITableSourceRelation> relationSet) {
        logger.info("***************Relation*****************");
        Integer countRelation = 0;
        if (relationSet != null) {
            for (BITableSourceRelation relation : relationSet) {
                logger.info("\nRelation " + (countRelation++) + relationLog("", relation));
                logger.info("\nRelation {}, ID:" + calculateRelationID(relation), countRelation);
            }
        }
        logger.info("***************Relation end*****************\n");

    }

    private void logPath(Set<BITableSourceRelationPath> relationPathSet) {
        logger.info("***************Path*****************");

        if (relationPathSet != null) {
            Integer countPath = 0;
            for (BITableSourceRelationPath path : relationPathSet) {
                String pathTitle = "\nPath:" + countPath++;
                Integer countRelation = 0;
                for (BITableSourceRelation relation : path.getAllRelations()) {
                    logger.info(BIStringUtils.append(
                            pathTitle,
                            "\nRelation " + (countRelation++),
                            relationLog("     ", relation)));
                }
                logger.info("\nPath:{} ID:" + calculatePathID(path), countPath);

            }
        }
        logger.info("***************Path end*****************\n");
    }

    private String calculateRelationID(BITableSourceRelation relation) {
        BICubeRelation cubeRelation = BICubeRelationUtils.convert(relation);
        ITableKey tableKey = cubeRelation.getPrimaryTable();
        BICubeTablePath relationPath = new BICubeTablePath();
        try {
            relationPath.addRelationAtHead(cubeRelation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.illegalArgument(relation.toString() + " the relation is so terrible");
        }
        return calculatePathID(tableKey, relationPath);
    }

    private String calculatePathID(BITableSourceRelationPath path) {

        BICubeTablePath cubeTablePath = BICubePathUtils.convert(path);

        ITableKey tableKey = null;
        try {
            tableKey = cubeTablePath.getFirstRelation().getPrimaryTable();
        } catch (BITablePathEmptyException e) {
            logger.error(e.getMessage(), e);
        }
        return calculatePathID(tableKey, cubeTablePath);
    }

    private String calculatePathID(ITableKey tableKey, BICubeTablePath relationPath) {
        return BICubeResourceRetrieval.calculateTableRelationSourceID(tableKey, relationPath);
    }

    private String relationLog(String prefix, BITableSourceRelation relation) {
        return BIStringUtils.append("\n",
                prefix + "       primaryTable:", relation.getPrimaryTable().getTableName() + "," + relation.getPrimaryTable().getSourceID(),
                "\n" + prefix + "       foreignTable:", relation.getForeignTable().getTableName() + "," + relation.getForeignTable().getSourceID(),
                "\n" + prefix + "       primaryField:", relation.getPrimaryField().getFieldName(),
                "\n" + prefix + "       foreignField:", relation.getForeignField().getFieldName());
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
        return this.getTaskId().equals(((BuildCubeTask) obj).getTaskId());
    }

}
