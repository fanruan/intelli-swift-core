package com.fr.bi.cal;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.provider.AllUserTravel;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel
 *         FIXME 功能代码质量严峻
 */
public class BICubeManager implements BICubeManagerProvider {

    private Map<Long, SingleUserCubeManager> userMap = new ConcurrentHashMap<Long, SingleUserCubeManager>();

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
    public CubeBuildStuffManager getGeneratingObject(long userId) {
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
    public Status getStatus(long userId) {
        return getCubeManager(userId).getStatus();
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

    /**
     * 生成cube
     */
    @Override
    public void generateCubes() {
        BIUserUtils.allUserTravelAction(new AllUserTravel() {

            @Override
            public void start(long userId) {
                getCubeManager(userId).generateCubes();
            }
        });
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
    public boolean isReplacing(long userId) {
        return getCubeManager(userId).isReplacing();
    }
}
