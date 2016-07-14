package com.finebi.cube.conf;


import com.finebi.cube.impl.conf.CubeBuildStaff;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;

import java.util.Iterator;

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
    void resetCubeGenerationHour(long userId);

    /**
     * 生成cube
     */
    void generateCubes();

    /**
     * 增加人物
     *
     * @param task 任务
     * @return 是否增加
     */
    boolean addTask(CubeTask task, long userId);

    CubeBuildStaff getGeneratingObject(long userId);

    void removeTask(String id, long userId);

    boolean hasAllTask(long userId);

    boolean hasCheckTask(long userId);

    boolean hasTask(CubeTask task, long userId);

    boolean hasTask(long useId);

    boolean hasWaitingCheckTask(long useId);

    Iterator<CubeTask> getWaitingTaskIterator(long userId);

    CubeTask getGeneratedTask(long userId);

    CubeTask getGeneratingTask(long userId);

    boolean checkCubeStatus(long userId);

    Status getStatus(long userId);

    boolean isReplacing(long userId);

}
