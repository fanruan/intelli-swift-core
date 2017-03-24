package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.*;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.impl.conf.*;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.utils.Collection2StringUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kary on 16/5/30.
 */
public class CubeBuildHelper {
    private static class CubeBuildHelperHolder {
        private static final CubeBuildHelper INSTANCE = new CubeBuildHelper();
    }

    private BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
    private LinkedBlockingQueue<CustomTableTask> taskQueue = new LinkedBlockingQueue();
    private boolean isCubeBuilding = false;
    private int retryTimes = 100;
    private int delayTimes = 5000;

    private CubeBuildHelper() {
        Thread taskAddThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                CustomTableTask taskInfo = taskQueue.take();
                                isCubeBuilding = true;
                                BILoggerFactory.getLogger().info("Update table ID:" + taskInfo.baseTableSourceIdToString());
                                int TIMES = 0;
                                for (int i = 0; i < retryTimes; i++) {
                                    if (!cubeManager.hasTask()) {
                                        CubeBuildCustomTables(taskInfo.getUserId(), taskInfo.getBaseTableSourceIdList(), taskInfo.getUpdateTypeList());
                                        isCubeBuilding = false;
                                        break;
                                    }
                                    long timeDelay = i * delayTimes;
                                    BILoggerFactory.getLogger(CubeBuildHelper.class).info("FineIndex is generating, wait to add SingleTable FineIndex Task until finished, retry times : " + i);
                                    BILoggerFactory.getLogger(CubeBuildHelper.class).info("the SingleTable SourceId is: " + taskInfo.baseTableSourceIdToString());
                                    try {
                                        Thread.sleep(timeDelay);
                                    } catch (InterruptedException e) {
                                        BILoggerFactory.getLogger(CubeBuildHelper.class).error(e.getMessage(), e);
                                    }
                                    TIMES++;
                                }
                                if (TIMES == retryTimes) {
                                    BILoggerFactory.getLogger(CubeBuildHelper.class).info("up to add SingleTable FineIndex Task retry times, Please add SingleTable Task again");
                                    BILoggerFactory.getLogger(CubeBuildHelper.class).info("the SingleTable SourceId is: " + taskInfo.baseTableSourceIdToString());
                                    isCubeBuilding = false;
                                }
                            } catch (Exception e) {
                                isCubeBuilding = false;
                                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
                            }
                        }
                    }
                }
        );
        taskAddThread.start();
    }

    public static CubeBuildHelper getInstance() {
        return CubeBuildHelperHolder.INSTANCE;
    }


    private interface TableTask {
        String baseTableSourceIdToString();

    }

    /**
     * 自定义更新表task
     */
    private class CustomTableTask implements TableTask {
        private long userId;
        private List<String> baseTableSourceIdList;
        private List<Integer> updateTypeList;

        public CustomTableTask(long userId, String baseTableSourceId, Integer updateType) {
            this.userId = userId;
            if (baseTableSourceIdList == null || updateTypeList == null) {
                baseTableSourceIdList = new ArrayList<String>();
                updateTypeList = new ArrayList<Integer>();
            }
            baseTableSourceIdList.add(baseTableSourceId);
            updateTypeList.add(updateType);
        }

        public CustomTableTask(long userId, List<String> baseTableSourceIdList, List<Integer> updateTypeList) {
            this.userId = userId;

            if (this.baseTableSourceIdList == null || this.updateTypeList == null) {
                this.baseTableSourceIdList = baseTableSourceIdList;
                this.updateTypeList = updateTypeList;
            } else {
                this.baseTableSourceIdList.addAll(baseTableSourceIdList);
                this.updateTypeList.addAll(updateTypeList);
            }
        }

        public CustomTableTask taskMerge(long userId, String baseTableSourceId, Integer updateType) {
            if (baseTableSourceIdList == null || updateTypeList == null) {
                baseTableSourceIdList = new ArrayList<String>();
                updateTypeList = new ArrayList<Integer>();
            }
            baseTableSourceIdList.add(baseTableSourceId);
            updateTypeList.add(updateType);
            return this;
        }

        public CustomTableTask taskMerge(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
            if (baseTableSourceIdList == null || updateTypeList == null) {
                baseTableSourceIdList = new ArrayList<String>();
                updateTypeList = new ArrayList<Integer>();
            }
            baseTableSourceIdList.addAll(baseTableSourceIds);
            updateTypeList.addAll(updateTypes);
            return this;
        }


        public long getUserId() {
            return userId;
        }

        public List<String> getBaseTableSourceIdList() {
            return baseTableSourceIdList;
        }

        public List<Integer> getUpdateTypeList() {
            return updateTypeList;
        }

        @Override
        public String baseTableSourceIdToString() {
            return Collection2StringUtils.collection2String(baseTableSourceIdList);
        }
    }

    public synchronized void addCustomTableTask2Queue(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes)
            throws InterruptedException {
        if (baseTableSourceIds.isEmpty() || updateTypes.isEmpty() || baseTableSourceIds.size() != updateTypes.size()) {
            BILoggerFactory.getLogger().error("Add single table task to queue failed");
            return;
        }
        BILoggerFactory.getLogger().info("Add single table task to queue:"
                + Collection2StringUtils.collection2String(baseTableSourceIds));
        if (taskQueue.isEmpty()) {
            taskQueue.put(new CustomTableTask(userId, baseTableSourceIds, updateTypes));
            BILoggerFactory.getLogger().info("TaskQueue is empty ! Add single table task: "
                    + Collection2StringUtils.collection2String(baseTableSourceIds) +
                    " , updateType : " + Collection2StringUtils.collection2String(updateTypes));
        } else {
            CustomTableTask task = taskQueue.poll();
            if (task != null) {
                taskQueue.put(task.taskMerge(userId, baseTableSourceIds, updateTypes));
                BILoggerFactory.getLogger().info("TaskQueue is not empty!Merge single table task: "
                        + Collection2StringUtils.collection2String(baseTableSourceIds)
                        + " , updateType : " + Collection2StringUtils.collection2String(updateTypes)
                        + " to:" + task.baseTableSourceIdToString());
            } else {
                taskQueue.put(new CustomTableTask(userId, baseTableSourceIds, updateTypes));
                BILoggerFactory.getLogger().info("TaskQueue is empty!Add single table task: "
                        + Collection2StringUtils.collection2String(baseTableSourceIds)
                        + " , updateType : " + Collection2StringUtils.collection2String(updateTypes));

            }
        }
    }

    public boolean hasWaitingTables() {
        return taskQueue.size() > 0 || isCubeBuilding;
    }

    /**
     * 自定义表更新
     *
     * @param userId
     * @param baseTableSourceIds
     * @param updateTypes
     */
    public void CubeBuildCustomTables(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        BILoggerFactory.getLogger().info("Update tables ID:" + baseTableSourceIds.toArray());
        List<CubeBuildStuff> cubeBuildList = buildCustomTable(userId, baseTableSourceIds, updateTypes);
        for (CubeBuildStuff cubeBuild : cubeBuildList) {
            cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
        }
    }

    public List<CubeBuildStuff> buildCustomTable(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        List<CubeBuildStuff> cubeBuildStuffList = new ArrayList<CubeBuildStuff>();

        Map<CubeTableSource, Set<String>> tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        Map<String, Set<Integer>> baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();

        for (int i = 0; i < baseTableSourceIds.size(); i++) {
            List<BusinessTable> tableList = getTablesContainsSourceId(userId, baseTableSourceIds.get(i), updateTypes.get(i));
            List<CubeTableSource> tableSourceList = getTableSourcesFromBusinessTables(tableList, userId, baseTableSourceIds.get(i));
            for (CubeTableSource tableSource : tableSourceList) {
                if (tableBaseSourceIdMap.containsKey(tableSource)) {
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                } else {
                    tableBaseSourceIdMap.put(tableSource, new HashSet<String>());
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                }

                if (baseSourceIdUpdateTypeMap.containsKey(baseTableSourceIds.get(i))) {
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                } else {
                    baseSourceIdUpdateTypeMap.put(baseTableSourceIds.get(i), new HashSet<Integer>());
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                }

            }

        }

        cubeBuildStuffList.add(getCubeBuildStuffCustomTables(userId, tableBaseSourceIdMap, baseSourceIdUpdateTypeMap));
        return cubeBuildStuffList;
    }

    private CubeBuildStuff getCubeBuildStuffCustomTables(long userId, Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                                                         Map<String, Set<Integer>> baseSourceIdUpdateTypeMap) {
        return new CubeBuildCustomTables(
                userId,
                tableBaseSourceIdMap,
                baseSourceIdUpdateTypeMap,
                getAbsentTable(userId),
                getAbsentRelation(userId),
                getAbsentPath(userId));
    }

    /**
     * 单表 更新
     *
     * @param userId
     * @param baseTableSourceId
     * @param updateType
     */
    public void CubeBuildSingleTable(long userId, String baseTableSourceId, int updateType) {
        BILoggerFactory.getLogger().info("Update table ID:" + baseTableSourceId);
        List<CubeBuildStuff> cubeBuildList = buildSingleTable(userId, baseTableSourceId, updateType);
        BILoggerFactory.getLogger().info("Update relevant table size:" + cubeBuildList.size());
        for (CubeBuildStuff cubeBuild : cubeBuildList) {
            cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
        }

    }

    public List<CubeBuildStuff> buildSingleTable(long userId, String baseTableSourceId, int updateType) {
        List<CubeBuildStuff> cubeBuildStuffList = new ArrayList<CubeBuildStuff>();
        List<BusinessTable> tableList = getTablesContainsSourceId(userId, baseTableSourceId, updateType);
        List<CubeTableSource> tableSourceList = getTableSourcesFromBusinessTables(tableList, userId, baseTableSourceId);
        for (CubeTableSource tableSource : tableSourceList) {
            cubeBuildStuffList.add(getCubeBuildStuffSpecificTable(userId, tableSource, baseTableSourceId, updateType));
        }
        return cubeBuildStuffList;
    }

    private CubeBuildStuff getCubeBuildStuffSpecificTable(long userId, CubeTableSource cubeTableSource, String baseTableSourceId, int updateType) {
        return new CubeBuildStuffSpecificTable(
                userId,
                cubeTableSource,
                baseTableSourceId,
                updateType,
                getAbsentTable(userId),
                getAbsentRelation(userId),
                getAbsentPath(userId));
    }


    /**
     * 检查ETL表的基础表，是否存在。
     *
     * @param userId
     * @param etlTableSource
     * @param basicTableID
     * @return
     */
    private boolean checkETLTable(long userId, ETLTableSource etlTableSource, String basicTableID) {
        List<Set<CubeTableSource>> layers = etlTableSource.createGenerateTablesList();
        for (CubeTableSource tableSource : CubeUpdateUtils.toSet(layers)) {
            if (TableSourceUtils.isBasicTable(tableSource)) {
                if (!CubeUpdateUtils.isTableExist(userId, tableSource) &&
                        !ComparatorUtils.equals(tableSource.getSourceID(), basicTableID)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 直接执行全局更新，不会进行check
     *
     * @param userId
     */
    public void CubeBuildStaffComplete(long userId) {
        BILoggerFactory.getLogger(this.getClass()).info(BIStringUtils.append(BIDateUtils.getCurrentDateTime(), " FineIndex all update start"));
        CubeBuildStuff cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
        CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
        cubeManager.addTask(task, userId);
    }

    public void CubeBuildStaff(long userId) {
        CubeBuildStuff cubeBuild;
        /**
         * 若cube不存在,全局更新
         * 若有新增表或者新增关联，增量更新，否则进行全量
         */
        StringBuffer msg = new StringBuffer();
        if (isPart(userId)) {
            msg.append(" FineIndex part update start" + "\n");
            Set<CubeTableSource> absentTables = getAbsentTable(userId);
            Set<BITableSourceRelation> absentRelations = getAbsentRelation(userId);
            Set<BITableSourceRelationPath> absentPaths = getAbsentPath(userId);
            cubeBuild = new CubeBuildStuffSupplement(userId, absentTables, absentRelations, absentPaths);
        } else if (isUpdateMeta(userId)) {
            msg.append(" FineIndex update meta data");
            cubeBuild = new CubeBuildStuffEmptyTable(userId);
        } else {
            msg.append(" FineIndex all update start");
            cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + " preCondition checking……");
        }
        if (preConditionsCheck(userId, cubeBuild)) {
            CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + msg);
            cubeManager.addTask(task, userId);
        }
    }

    /**
     * @param userId
     * @return
     */
    private boolean isUpdateMeta(long userId) {
        /**
         * 关联减少，表没有变化
         */
        boolean relationReduced = BICubeConfigureCenter.getTableRelationManager().isRelationReduced(userId) &&
                BICubeConfigureCenter.getPackageManager().isTableNoChange(userId);
        /**
         * 表减少，关联没有变化
         */
        boolean tableReduced = BICubeConfigureCenter.getPackageManager().isTableReduced(userId) &&
                BICubeConfigureCenter.getTableRelationManager().isRelationNoChange(userId);
        /**
         * 关联和表都减少了
         */
        boolean tableRelationReduced = BICubeConfigureCenter.getPackageManager().isTableReduced(userId) &&
                BICubeConfigureCenter.getTableRelationManager().isRelationReduced(userId);
        return relationReduced || tableReduced || tableRelationReduced;
    }

    private Set<CubeTableSource> getAbsentTable(long userId) {
        return CubeUpdateUtils.getCubeAbsentTables(userId);
    }

    private Set<BITableSourceRelation> getAbsentRelation(long userId) {
        return CubeUpdateUtils.getCubeAbsentRelations(userId);
    }

    private Set<BITableSourceRelationPath> getAbsentPath(long userId) {
        return CubeUpdateUtils.getCubeAbsentPaths(userId);

    }

    private boolean isPart(long userId) {
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(userId)));
        Cube cube = new BICube(resourceRetrievalService, discovery);
        boolean isPart = (CubeUpdateUtils.getCubeAbsentTables(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentRelations(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentPaths(userId).size() > 0) && cube.isVersionAvailable();
        return isPart;
    }

    private boolean preConditionsCheck(long userId, CubeBuildStuff cubeBuild) {
        boolean conditionsMeet = cubeBuild.preConditionsCheck();
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed!";
            BILoggerFactory.getLogger(CubeBuildHelper.class).error(errorMessage);
            BIConfigureManagerCenter.getLogManager().logEnd(userId);
        }
        return conditionsMeet;
    }

    private List<BusinessTable> getTablesContainsSourceId(long userId, String baseTableSourceId, int updateType) {
        List<BusinessTable> tableList = new ArrayList<BusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            List<Set<CubeTableSource>> sourceList = businessTable.getTableSource().createGenerateTablesList();
            for (Set<CubeTableSource> sourceSet : sourceList) {
                Iterator iterator = sourceSet.iterator();
                while (iterator.hasNext()) {
                    CubeTableSource tableSource = (CubeTableSource) iterator.next();
                    if (ComparatorUtils.equals(tableSource.getSourceID(), baseTableSourceId)) {
                        tableList.add(businessTable);
                    }
                }
            }
        }
        return tableList;
    }

    private List<CubeTableSource> getTableSourcesFromBusinessTables(List<BusinessTable> tableList, long userId, String baseTableSourceId) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        for (BusinessTable table : tableList) {
            if (table.getTableSource() instanceof ETLTableSource) {
                if (checkETLTable(userId, (ETLTableSource) table.getTableSource(), baseTableSourceId)) {
                    tableSources.add(table.getTableSource());
                }
            } else {
                tableSources.add(table.getTableSource());
            }
        }
        return removeDuplicateSources(tableSources);
    }

    private List<CubeTableSource> removeDuplicateSources(List<CubeTableSource> sourceList) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        Set<String> sourceIds = new HashSet<String>();
        for (CubeTableSource source : sourceList) {
            if (!sourceIds.contains(source.getSourceID())) {
                sourceIds.add(source.getSourceID());
                tableSources.add(source);
            }
        }
        return tableSources;
    }
}
