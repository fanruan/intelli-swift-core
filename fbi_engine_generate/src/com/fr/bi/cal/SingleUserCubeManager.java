package com.fr.bi.cal;

import com.finebi.cube.impl.conf.CubeBuildStaff;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.CubeRunner;
import com.fr.bi.cal.generate.TimerRunner;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;

import java.util.Iterator;


public class SingleUserCubeManager {

    /**
     *
     */
    private static final long serialVersionUID = 6187369933206388925L;
    private CubeRunner runner;
    protected BIUser biUser;
    private TimerRunner timerRunner;


    public SingleUserCubeManager(long userId) {
        biUser = new BIUser(userId);
        runner = new CubeRunner(userId);
        timerRunner = new TimerRunner(userId);

        User user;
        try {
            user = UserControl.getInstance().getUser(userId);
            if (user != null) {
                BILogger.getLogger().info(BIDateUtils.getCurrentDateTime() + "User :" + user.getUsername() + "BI Service Start");
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


    /**
     * 生成cube
     */
    public void generateCubes() {
        runner.generateCubes();
    }

    public CubeBuildStaff getGeneratingObject() {
        return runner.getCubeGeneratingObjects();
    }

    public CubeTask getGeneratedTask() {
        return runner.getGeneratedTask();
    }

    public CubeTask getGeneratingTask() {
        return runner.getGeneratingTask();
    }

    public boolean hasTask(CubeTask t) {
        return runner.hasTask(t);
    }

    public boolean hasTask() {
        return runner.hasTask();
    }

    public boolean hasWaitingCheckTask() {
        return runner.hasWaitingCheckTask();
    }


    public Iterator<CubeTask> getWaitingTaskIterator() {
        return runner.getWaitingList();
    }

    public boolean hasAllTask() {
        Iterator<CubeTask> it = getWaitingTaskIterator();
        while (it.hasNext()) {
            CubeTask ct = it.next();
            if (ct.getTaskType() == CubeTaskType.ALL) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCheckTask() {
        Iterator<CubeTask> it = getWaitingTaskIterator();
        while (it.hasNext()) {
            CubeTask ct = it.next();
            if (ct.getTaskType() == CubeTaskType.CHECK) {
                return true;
            }
        }
        return false;
    }

    public void removeTask(String uuid) {
        runner.removeTask(uuid);
    }

    /**
     * 若存在相同任务则返回false,不添加
     * 添加成功返回true
     *
     * @param t 任务
     * @return true或false
     */
    public boolean addTask(CubeTask t) {
        if (hasTask(t)) {
            return false;
        }
        runner.addTask(t);
        return true;
    }

    public void resetCubeGenerationHour() {
        timerRunner.reGenerateTimeTasks();
    }

    /**
     * 检查cube生成状态
     *
     * @return true或false
     */
    public boolean checkCubeStatus() {
        return runner.getStatue() == Status.LOADING
                || isReplacing();
    }

    public boolean isReplacing() {
        return runner.getStatue() == Status.REPLACING;
    }

    public Status getStatus() {
        return runner.getStatue();
    }

    /**
     * 环境改变
     */
    public void envChanged() {
        runner.envChanged();
        timerRunner.envChanged();
    }

}
