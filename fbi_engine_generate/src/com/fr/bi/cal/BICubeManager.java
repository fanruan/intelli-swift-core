package com.fr.bi.cal;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.*;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.generate.CustomTaskBuilder;
import com.fr.bi.cal.generate.queue.CubeGenerateTaskQueue;
import com.fr.bi.cal.generate.task.AllCubeGenerateTask;
import com.fr.bi.cal.generate.task.CustomCubeGenerateTask;
import com.fr.bi.cal.generate.task.SingleCubeGenerateTask;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.structure.queue.CubeTaskCondition;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

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

    private ICubeGenerateTask cubeGenerateTask;

    private boolean isCubeBuilding = false;

    private Object object = new Object();

    private CubeGenerateTaskQueue cubeGenerateTaskQueue = new CubeGenerateTaskQueue();

    private static BILogger LOGGER = BILoggerFactory.getLogger(BICubeManager.class);

    public BICubeManager() {
        CubeBuildRunnable cubeBuildRunnable = new CubeBuildRunnable();
        new Thread(cubeBuildRunnable).start();
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
            boolean result = addCubeGenerateTask2Queue(userId, baseTableSourceId, updateType, false);
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
        return cubeGenerateTaskQueue.getSize() > 0 || isCubeBuilding;
    }

    private void startCubeBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logAllCubeBuildingStatus(userId, true);
        isCubeBuilding = true;
    }

    private void finishCubeBuilding(long userId) {
        BIConfigureManagerCenter.getLogManager().logAllCubeBuildingStatus(userId, false);
        isCubeBuilding = false;
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
        tableSourceIdSet.addAll(cubeGenerateTaskQueue.getTableSourceIds());

        synchronized (object) {
            if (cubeGenerateTask != null) {
                tableSourceIdSet.add(cubeGenerateTask.getTableSourceId());
            }
        }
        return tableSourceIdSet;
    }


    @Override
    public synchronized boolean addCubeGenerateTask2Queue(long userId, String baseTableSourceId, Integer updateType, boolean isTimedTask) {
        try {
            //BI-8384  cube更新，不区分userId了。。都用-999；
            userId = UserControl.getInstance().getSuperManagerID();
            BILoggerFactory.getLogger(BICubeManager.class).info("Add CubeGenerateTask to taskqueue!" + (isTimedTask ? "(TimedTask)" : "(ManualTask)"));
            ICubeGenerateTask cubeGenerateTask;
            if (baseTableSourceId != null) {
                cubeGenerateTask = new SingleCubeGenerateTask(baseTableSourceId, updateType, userId);
                cubeGenerateTaskQueue.put(cubeGenerateTask);
            } else {
                if (isTimedTask) {
                    cubeGenerateTask = new AllCubeGenerateTask(userId);
                    cubeGenerateTaskQueue.put(cubeGenerateTask);
                } else {
                    cubeGenerateTask = CustomTaskBuilder.getCubeGenerateTask(userId);
                    cubeGenerateTaskQueue.put(cubeGenerateTask);
                }
            }
            BILoggerFactory.getLogger(BICubeManager.class).info(cubeGenerateTask.getTaskInfo());
//            CubeGenerateTaskQueue.getInstance().showTasks();
        } catch (Exception e) {
            BILoggerFactory.getLogger(BICubeManager.class).error("CubeGenerateTask:" +
                    ((baseTableSourceId != null) ? baseTableSourceId : "All") + " add to task queue failed！", e);
            return false;
        }
        return true;

    }

    public boolean addCubeGenerateTask2Queue(long userId, boolean isTimedTask,
                                             List<BITableRelation> tableRelations, Map<String, Integer> sourceIdUpdateTypeMap) {
        try {
            //BI-8384  cube更新，不区分userId了。。都用-999；
            userId = UserControl.getInstance().getSuperManagerID();
            BILoggerFactory.getLogger(BICubeManager.class).info("Add Custom CubeGenerateTask to taskqueue!" + (isTimedTask ? "(TimedTask)" : "(ManualTask)"));
            Map<String, List<Integer>> sourceIdUpdateTypesMap = new HashMap<String, List<Integer>>();
            for (Map.Entry<String, Integer> entry : sourceIdUpdateTypeMap.entrySet()) {
                sourceIdUpdateTypesMap.put(entry.getKey(), new ArrayList<Integer>());
                sourceIdUpdateTypesMap.get(entry.getKey()).add(entry.getValue());
            }
            ICubeGenerateTask cubeGenerateTask = new CustomCubeGenerateTask(userId, sourceIdUpdateTypesMap, tableRelations);
            cubeGenerateTaskQueue.put(cubeGenerateTask);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BICubeManager.class).error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private class CubeBuildRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (hasTask()) {
                        CubeTaskCondition.getInstance().await();
                    }
                    cubeGenerateTask = cubeGenerateTaskQueue.take();
                    //合并
                    cubeGenerateTask = mergeTaskIfNeed(cubeGenerateTask);
                    long userId = cubeGenerateTask.getUserId();
                    startCubeBuilding(userId);
                    ITaskCalculator taskCalculator = cubeGenerateTask.getTaskCalculator();
                    BISystemConfigHelper configHelper = new BISystemConfigHelper();
                    Set<CubeTableSource> allTableSources = configHelper.extractTableSource(configHelper.getSystemBusinessTables());
                    Set<BITableSourceRelation> allRelations = configHelper.convertRelations(configHelper.getSystemTableRelations());
                    Set<BITableSourceRelationPath> allPaths = configHelper.convertPaths(configHelper.getSystemTablePaths());
                    CubeBuildStuff cubeBuildStuff = taskCalculator.generateCubeBuildStuff(allTableSources, allRelations, allPaths);
                    if (cubeBuildStuff.isNeed2Update()) {
                        CubeTask cubeTask = new BuildCubeTask(new BIUser(userId), cubeBuildStuff);
                        addTask(cubeTask, userId);
                    }

                    finishCubeBuilding(userId);
                } catch (Exception e) {
                    BILoggerFactory.getLogger(BICubeManager.class).error(e.getMessage(), e);
                } finally {
                    synchronized (object) {
                        cubeGenerateTask = null;
                    }
                }
            }
        }

        /**
         * 当前任务是全局、单表、自定义更新任务时，则往后取任务，如果取出的不是check和empty，则合并，直到下一个任务是check和empty。
         * 当前任务是check和empty时，不合并。
         *
         * @param mainCubeGenerateTask
         */
        private ICubeGenerateTask mergeTaskIfNeed(ICubeGenerateTask mainCubeGenerateTask) throws InterruptedException {
            BILoggerFactory.getLogger(BICubeManager.class).info("=============Begin to merge task!============");
            BILoggerFactory.getLogger(BICubeManager.class).info("mainCubeGenerateTask:" + mainCubeGenerateTask.getTaskInfo());
            if (mainCubeGenerateTask.isOk2Merge()) {
                while (cubeGenerateTaskQueue.peek() != null) {
                    ICubeGenerateTask nextCubeGenerateTask = cubeGenerateTaskQueue.peek();
                    BILoggerFactory.getLogger(BICubeManager.class).info("nextCubeGenerateTask:" + nextCubeGenerateTask.getTaskInfo());
                    if (nextCubeGenerateTask.isOk2Merge()) {
                        mainCubeGenerateTask = mainCubeGenerateTask.merge(nextCubeGenerateTask);
                        cubeGenerateTaskQueue.poll();
                        continue;
                    }
                    break;
                }
            }
            BILoggerFactory.getLogger(BICubeManager.class).info("=============Task merge end!============");
            return mainCubeGenerateTask;
        }
    }

    @Override
    public CubeTask getUpdatingTask(long userId) {
        SingleUserCubeManager singleUserCubeManager = getCubeManager(userId);
        return singleUserCubeManager.getUpdatingTask();
    }

    public List<ICubeGenerateTask> getCubeGenerateTasks() {
        return cubeGenerateTaskQueue.getCubeGenerateTasks();
    }

    public boolean removeCubeGenerateTask(ICubeGenerateTask task) {
        return cubeGenerateTaskQueue.remove(task);
    }

    public boolean removeCubeGenerateTask(List<ICubeGenerateTask> tasks) {
        for (ICubeGenerateTask task : tasks) {
            cubeGenerateTaskQueue.remove(task);
        }
        return true;
    }
}
