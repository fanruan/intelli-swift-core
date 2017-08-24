package com.finebi.cube.conf;


import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BICubeManagerProvider {

    String XML_TAG = "BICubeManager";

    /**
     * 环境改变
     */
    void envChanged();

    /**
     * 重置cube生成时间
     */
    void resetCubeTimerTasks(long userId);

    /**
     * 增加任务
     *
     * @param task 任务
     * @return 是否增加
     */
    boolean addTask(CubeTask task, long userId);

    CubeBuildStuffComplete getGeneratingObject(long userId);

    void removeTask(String id, long userId);

    boolean hasAllTask(long userId);

    boolean hasCheckTask(long userId);

    boolean hasTask(CubeTask task, long userId);

    boolean hasTask(long useId);

    /**
     * 获取所有用户是否有cube生成任务
     *
     * @return
     */
    boolean hasTask();

    boolean hasWaitingCheckTask(long useId);

    Iterator<CubeTask> getWaitingTaskIterator(long userId);

    CubeTask getGeneratedTask(long userId);

    CubeTask getGeneratingTask(long userId);

    boolean checkCubeStatus(long userId);

    void setStatus(long userId, Status status);

    boolean cubeTaskBuild(long userId, String baseTableSourceId, int updateType);

    Set<String> getCubeGeneratingTableSourceIds(long userId);

    Set<String> getCubeWaiting2GenerateTableSourceIds(long userId);

    boolean isCubeBuilding();

    Set<String> getAllCubeWaiting2GenerateTableSouceIds(long userId);

    CubeTask getUpdatingTask(long userId);

    /**
     * 添加单表、全局、check、empty任务用
     *
     * @param userId
     * @param baseTableSourceId
     * @param updateType
     * @param isTimedTask       是否定时更新触发的任务
     * @return
     */
    boolean addCubeGenerateTask2Queue(long userId, String baseTableSourceId, Integer updateType, boolean isTimedTask);

    /**
     * 添加custom任务用
     *
     * @param userId
     * @param isTimedTask
     * @param tableRelations
     * @param sourceIdUpdateTypeMap 基础表id--更新类型
     * @return
     */
    boolean addCubeGenerateTask2Queue(long userId, boolean isTimedTask, List<BITableRelation> tableRelations, Map<String, Integer> sourceIdUpdateTypeMap);

    List<ICubeGenerateTask> getCubeGenerateTasks();

    boolean removeCubeGenerateTask(ICubeGenerateTask task);

    boolean removeCubeGenerateTask(List<ICubeGenerateTask> tasks);
}
