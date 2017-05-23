package com.fr.bi.cal;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.generate.CustomTableTask;
import com.fr.bi.cal.generate.CustomTaskBuilder;
import com.fr.bi.cal.generate.queue.CustomTaskQueue;
import com.fr.bi.cal.utils.Collection2StringUtils;
import com.fr.bi.cal.utils.Single2CollectionUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel
 *         FIXME 功能代码质量严峻
 *         亟待重构
 */
public class BICubeManager implements BICubeManagerProvider {

    private Map<Long, SingleUserCubeManager> userMap = new ConcurrentHashMap<Long, SingleUserCubeManager>();

    public BICubeManager(Map<Long, SingleUserCubeManager> userMap) {
        this.userMap = userMap;
    }

    private CustomTableTask taskInfo;

    private CustomTaskBuilder customTaskBuilder = new CustomTaskBuilder();

    private boolean isSingleTableCubeBuilding = false;
    private boolean isAllCubeBuilding = false;

    private Object object = new Object();

    private int retryTimes = 100;

    private int delayTimes = 5000;

    private static BILogger LOGGER = BILoggerFactory.getLogger(BICubeManager.class);

    public BICubeManager() {
        Thread taskAddThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                taskInfo = CustomTaskQueue.getInstance().take();
                                startSingleTableBuilding(taskInfo.getUserId());
                                LOGGER.info("Update table ID:" + taskInfo.baseTableSourceIdToString());
                                int times = 0;
                                for (int i = 0; i < retryTimes; i++) {
                                    if (!hasTask()) {
                                        List<CubeBuildStuff> cubeBuildStuffList = customTaskBuilder.CubeBuildCustomTables(taskInfo.getUserId(), taskInfo.getBaseTableSourceIdList(), taskInfo.getUpdateTypeList());
                                        for (CubeBuildStuff cubeBuildStuff : cubeBuildStuffList) {
                                            addTask(new BuildCubeTask(new BIUser(taskInfo.getUserId()), cubeBuildStuff), taskInfo.getUserId());
                                        }
                                        finishSingleTableBuilding(taskInfo.getUserId());
                                        break;
                                    }
                                    long timeDelay = i * delayTimes;
                                    LOGGER.info("Cube is generating, wait to add SingleTable Cube Task until finished, retry times : " + i);
                                    LOGGER.info("the SingleTable SourceId is: " + taskInfo.baseTableSourceIdToString());
                                    try {
                                        Thread.sleep(timeDelay);
                                    } catch (InterruptedException e) {
                                        LOGGER.error(e.getMessage(), e);
                                    }
                                    times++;
                                }
                                if (times == retryTimes) {
                                    LOGGER.info("up to add SingleTable Cube Task retry times, Please add SingleTable Task again");
                                    LOGGER.info("the SingleTable SourceId is: " + taskInfo.baseTableSourceIdToString());
                                    finishSingleTableBuilding(taskInfo.getUserId());
                                }
                            } catch (Exception e) {
                                finishSingleTableBuilding(taskInfo.getUserId());
                                LOGGER.error(e.getMessage(), e);
                            } finally {
                                synchronized (object) {
                                    taskInfo = null;
                                }
                            }
                        }
                    }
                }
        );
        taskAddThread.start();
    }

    public SingleUserCubeManager getCubeManager(long userId) {
        return BIConstructorUtils.constructObject(userId, SingleUserCubeManager.class, userMap);
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                CubeGenerationManager.getCubeManager().envChanged();
            }
        });
    }


    @Override
    public CubeBuildStuffComplete getGeneratingObject(long userId) {
        return getCubeManager(userId).getGeneratingObject();
    }

    @Override
    public CubeTask getGeneratedTask(long userId) {
        return getCubeManager(userId).getGeneratedTask();
    }

    @Override
    public CubeTask getGeneratingTask(long userId) {
        return getCubeManager(userId).getGeneratingTask();
    }

    @Override
    public boolean checkCubeStatus(long userId) {
        return getCubeManager(userId).checkCubeStatus();
    }

    @Override
    public void setStatus(long userId, Status status) {
        getCubeManager(userId).setStatus(status);
    }

    /**
     * 若存在相同任务则返回false,不添加
     * 添加成功返回true
     *
     * @param userId 用户id
     * @param t      任务
     * @return true或false
     */
    @Override
    public boolean addTask(CubeTask t, long userId) {
        return getCubeManager(userId).addTask(t);
    }

    /**
     * 是否有任务
     *
     * @param t      任务
     * @param userId 用户id
     * @return true或false
     */
    @Override
    public boolean hasTask(CubeTask t, long userId) {
        return getCubeManager(userId).hasTask(t);
    }


    @Override
    public boolean hasTask(long userId) {
        return getCubeManager(userId).hasTask();
    }

    @Override
    public boolean hasTask() {
        boolean result = false;
        for (long userId : userMap.keySet()) {
            result = (result || getCubeManager(userId).hasTask());
        }
        return result;
    }

    @Override
    public boolean hasWaitingCheckTask(long userId) {
        return getCubeManager(userId).hasWaitingCheckTask();
    }

    /**
     * 移除任务
     *
     * @param userId 用户id
     * @param uuid   uuid号
     */
    @Override
    public void removeTask(String uuid, long userId) {
        getCubeManager(userId).removeTask(uuid);
    }

    @Override
    public boolean hasAllTask(long userId) {
        return getCubeManager(userId).hasAllTask();
    }

    @Override
    public boolean hasCheckTask(long userId) {
        return getCubeManager(userId).hasCheckTask();
    }

    @Override
    public Iterator<CubeTask> getWaitingTaskIterator(long userId) {
        return getCubeManager(userId).getWaitingTaskIterator();
    }

    /**
     * 重置cube生成时间
     *
     * @param userId 用户id
     */
    @Override
    public void resetCubeGenerationHour(final long userId) {
        getCubeManager(userId).resetCubeGenerationHour();
    }


    @Override
    public void envChanged() {
        synchronized (this) {
            for (Entry<Long, SingleUserCubeManager> entry : userMap.entrySet()) {
                SingleUserCubeManager manager = entry.getValue();
                if (manager != null) {
                    manager.envChanged();
                }
            }
            userMap.clear();
        }
    }

    @Override
    public boolean cubeTaskBuild(long userId, String baseTableSourceId, int updateType) {
        try {
            boolean result = true;
            if (StringUtils.isEmpty(baseTableSourceId)) {
                CubeTask cubeTask = this.buildStaff(userId);
                if (cubeTask != null) {
                    result = addTask(cubeTask, userId);
                }
            } else {
                addCustomTableTask2Queue(userId, Single2CollectionUtils.toList(baseTableSourceId),
                        Single2CollectionUtils.toList(updateType));
            }
            BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
            BIConfigureManagerCenter.getCubeConfManager().updateMultiPathLastCubeStatus(BIReportConstant.MULTI_PATH_STATUS.NOT_NEED_GENERATE_CUBE);
            BIConfigureManagerCenter.getCubeConfManager().persistData(userId);
            return result;
        } catch (Exception e) {
            CubeGenerationManager.getCubeManager().setStatus(userId, Status.WRONG);
            LOGGER.error("FineIndex task build failed" + "\n" + e.getMessage(), e);
            return false;
        } finally {
            CubeGenerationManager.getCubeManager().setStatus(userId, Status.END);
        }
    }

    @Override
    public Set<String> getCubeGeneratingTableSourceIds(long userId) {
        return getCubeManager(userId).getCubeGeneratingTableSourceIds();
    }

    @Override
    public Set<String> getCubeWaiting2GenerateTableSourceIds(long userId) {
        return getCubeManager(userId).getCubeWaiting2GenerateSourceIds();
    }

    @Override
    public boolean isCubeBuilding() {
        return CustomTaskQueue.getInstance().getSize() > 0 || isSingleTableCubeBuilding || isAllCubeBuilding;
    }

    @Override
    public synchronized void addCustomTableTask2Queue(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes)
            throws InterruptedException {
        if (baseTableSourceIds.isEmpty() || updateTypes.isEmpty() || baseTableSourceIds.size() != updateTypes.size()) {
            LOGGER.error("Add single table task to queue failed");
            return;
        }
        LOGGER.info("Add single table task to queue:"
                + Collection2StringUtils.collection2String(baseTableSourceIds));
        if (CustomTaskQueue.getInstance().isEmpty()) {
            CustomTaskQueue.getInstance().put(new CustomTableTask(userId, baseTableSourceIds, updateTypes));
            LOGGER.info("TaskQueue is empty ! Add single table task: "
                    + Collection2StringUtils.collection2String(baseTableSourceIds) +
                    " , updateType : " + Collection2StringUtils.collection2String(updateTypes));
        } else {
            CustomTableTask task = CustomTaskQueue.getInstance().poll();
            if (task != null) {
                CustomTaskQueue.getInstance().put(task.taskMerge(userId, baseTableSourceIds, updateTypes));
                LOGGER.info("TaskQueue is not empty!Merge single table task: "
                        + Collection2StringUtils.collection2String(baseTableSourceIds)
                        + " , updateType : " + Collection2StringUtils.collection2String(updateTypes)
                        + " to:" + task.baseTableSourceIdToString());
            } else {
                CustomTaskQueue.getInstance().put(new CustomTableTask(userId, baseTableSourceIds, updateTypes));
                LOGGER.info("TaskQueue is empty!Add single table task: "
                        + Collection2StringUtils.collection2String(baseTableSourceIds)
                        + " , updateType : " + Collection2StringUtils.collection2String(updateTypes));

            }
        }
    }

    @Override
    public CubeTask buildCompleteStuff(long userId) {
        startAllCubeBuilding(userId);
        CubeTask cubeTask = customTaskBuilder.buildCompleteStuff(userId);
        finishAllCubeBuilding(userId);
        return cubeTask;
    }

    @Override
    public CubeTask buildStaff(long userId) {
        startAllCubeBuilding(userId);
        CubeTask cubeTask = customTaskBuilder.buildStaff(userId);
        finishAllCubeBuilding(userId);
        return cubeTask;
    }

    private void startAllCubeBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logAllCubeBuildingStatus(userId, true);
        isAllCubeBuilding = true;
    }

    private void finishAllCubeBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logAllCubeBuildingStatus(userId, false);
        isAllCubeBuilding = false;
    }

    private void startSingleTableBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logSingleTableBuildingStatus(userId, true);
        isSingleTableCubeBuilding = true;
    }

    private void finishSingleTableBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logSingleTableBuildingStatus(userId, false);
        isSingleTableCubeBuilding = false;
    }

    /**
     * 等待队列中的sourceIds
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getAllCubeWaiting2GenerateTableSouceIds(long userId) {
        Set<String> tableSourceIdSet = new HashSet<String>();
        tableSourceIdSet.addAll(getCubeWaiting2GenerateTableSourceIds(userId));
        Iterator<CustomTableTask> taskIterator = CustomTaskQueue.getInstance().getQueue().iterator();
        while (taskIterator.hasNext()) {
            CustomTableTask task = taskIterator.next();
            tableSourceIdSet.addAll(task.getBaseTableSourceIdList());
        }
        synchronized (object) {
            if (taskInfo != null) {
                tableSourceIdSet.addAll(taskInfo.getBaseTableSourceIdList());
            }
        }
        return tableSourceIdSet;
    }

    @Override
    public List<CubeBuildStuff> buildCustomTable(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        return customTaskBuilder.buildCustomTable(userId, baseTableSourceIds, updateTypes);
    }

    @Override
    public CubeTask getUpdatingTask(long userId) {
        SingleUserCubeManager singleUserCubeManager = getCubeManager(userId);
        return singleUserCubeManager.getUpdatingTask();
    }
}
