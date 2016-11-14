package com.fr.bi.cal.generate;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.structure.queue.QueueThread;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.io.File;
import java.util.Iterator;

/**
 * Created by GUY on 2015/3/16.
 */
public class CubeRunner {

    /**
     *
     */
    private static final long serialVersionUID = -249303913165576913L;

    protected volatile Status statue = Status.UNLOAD;
    protected BIUser biUser;
    QueueThread<CubeTask> cubeThread = new QueueThread<CubeTask>();
    private CubeBuildStuffComplete object;

    public CubeRunner(long userId) {
        biUser = new BIUser(userId);
        init();
    }

    public void envChanged() {
        synchronized (cubeThread) {
            cubeThread.clear();
        }
    }

    private void init() {

        //设置回调函数
        cubeThread.setTraversal(new Traversal<CubeTask>() {
            @Override
            public void actionPerformed(CubeTask cubeTask) {
                long start = System.currentTimeMillis();
                setStatue(Status.LOADING);
                start();
                try {
                    cubeTask.start();
                    cubeTask.run();
                    cubeTask.end();
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    finish(cubeTask);
                    setStatue(Status.LOADED);
                    BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + " Build OLAP database Cost:" + DateUtils.timeCostFrom(start));
                }
            }
        });
        //设置检查任务
        cubeThread.setCheck(new BrokenTraversal<CubeTask>() {
            @Override
            public boolean actionPerformed(CubeTask obj) {
                return checkCubePath();
            }
        });
        //执行线程队列
        cubeThread.start();
    }

    /**
     * 生成cube
     */
    public void generateCubes() {
        synchronized (cubeThread) {
            new Thread() {
                @Override
                public void run() {
                    generateCube();
                }
            }.start();
        }
    }

    public boolean hasTask(CubeTask t) {
        Iterator<CubeTask> iterator = cubeThread.iterator();
        while (iterator.hasNext()) {
            CubeTask task = iterator.next();
            if (ComparatorUtils.equals(task.getTaskId(), t.getTaskId())) {
                return true;
            }
        }
        return false;
//        return cubeThread.contains(task);
    }

    public boolean hasTask() {
        return !cubeThread.isEmpty();
    }

    public boolean hasWaitingCheckTask() {
        Iterator<CubeTask> iter = cubeThread.iterator();
        while (iter.hasNext()) {
            CubeTask ct = iter.next();
            if (ct.getTaskType() != CubeTaskType.SINGLE) {
                return true;
            }
        }
        return false;
    }

    public void addTask(CubeTask task) {
        cubeThread.add(task);
    }

    public void removeTask(String taskId) {
        Iterator<CubeTask> it = cubeThread.iterator();
        while (it.hasNext()) {
            CubeTask task = it.next();
            if (ComparatorUtils.equals(task.getTaskId(), taskId)) {
                it.remove();
            }
        }
    }

    public Iterator<CubeTask> getWaitingList() {
        return cubeThread.iterator();
    }

    private void generateCube() {
//        setStatue(Status.LOADED);
//        CubeBuildStuff cubeBuild = new CubeBuildStuffIncreased(biUser.getUserId(), CubeUpdateUtils.getCubeAbsentTables(biUser.getUserId()), CubeUpdateUtils.getCubeAbsentRelations(biUser.getUserId()));
//        CubeTask task = new BuildCubeTask(biUser, cubeBuild);
//        CubeGenerationManager.getCubeManager().addTask(task, biUser.getUserId());
    }

    private void start() {
        BackUpUtils.backup();
    }

    private void finish(CubeTask cubeTask) {
        long t = System.currentTimeMillis();
        try {

//            BICubeConfigureCenter.getPackageManager().finishGenerateCubes(biUser.getUserId());
            if (!cubeTask.getTaskType().equals(CubeTaskType.INSTANT)) {
                BILoggerFactory.getLogger().info("start to persist meta data!");
                BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
                BICubeConfigureCenter.getPackageManager().persistData(biUser.getUserId());
                BICubeConfigureCenter.getDataSourceManager().persistData(biUser.getUserId());
            }
            BILoggerFactory.getLogger().info("meta data finished! time cost: " + DateUtils.timeCostFrom(t));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        BICubeManager.getInstance().fetchCubeLoader(biUser.getUserId()).clear();
        /* 前台进度条完成进度最多到90%，当cube文件替换完成后传入调用logEnd，进度条直接到100%*/
        BIConfigureManagerCenter.getLogManager().logEnd(biUser.getUserId());
    }


    public CubeBuildStuffComplete getCubeGeneratingObjects() {
        if (object == null) {
            object = new CubeBuildStuffComplete(biUser);
            object.initialCubeStuff();
        }
        return object;
    }

    public Status getStatue() {
        return statue;
    }

    public void setStatue(Status statue) {
        this.statue = statue;
    }

    public CubeTask getGeneratingTask() {
        return cubeThread.getGenerating();
    }

    public CubeTask getGeneratedTask() {
        return cubeThread.getGenerated();
    }

    /**
     * 检查cube路径
     *
     * @return true或false
     */
    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIPathUtils.createBasePath()));
    }


}
