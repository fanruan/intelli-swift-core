package com.fr.bi.cal.generate;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeBuildStuffManager;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.common.inter.BrokenTraversal;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.Status;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.structure.queue.QueueThread;
import com.fr.bi.stable.utils.code.BILogger;
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
    private CubeBuildStuffManager object;

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
                        } catch (Exception e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        } finally {
                            cubeTask.end();
                            finish();
                            setStatue(Status.LOADED);
                            BILogger.getLogger().info(BIDateUtils.getCurrentDateTime() + " Build OLAP database Cost:" + DateUtils.timeCostFrom(start));
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

    public boolean hasTask(CubeTask task) {
        return cubeThread.contains(task);
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

    public void removeTask(String uuid) {
        Iterator<CubeTask> it = cubeThread.iterator();
        while (it.hasNext()) {
            CubeTask task = it.next();
            if (ComparatorUtils.equals(task.getUUID(), uuid)) {
                it.remove();
            }
        }
    }

    public Iterator<CubeTask> getWaitingList() {
        return cubeThread.iterator();
    }

    private void generateCube() {
        setStatue(Status.LOADED);
        CubeBuildStuff cubeBuildStuff= new CubeBuildStuffManager(new BIUser((biUser.getUserId())));
        CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(biUser,cubeBuildStuff),biUser.getUserId());
    }

    private void start() {
        backup();
    }

    private void backup() {
        BackUpUtils.backup();
    }

    private void finish() {
        setStatue(Status.REPLACING);
        BILogger.getLogger().info("Start Replacing Old Cubes, Stop All Analysis");
        long start = System.currentTimeMillis();
        CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId()).clear();
        CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId()).clear();
        BICubeManager.getInstance().fetchCubeLoader(biUser.getUserId()).clear();
        renameToCurrentDirect();
        setStatue(Status.LOADED);
        BILogger.getLogger().info("Replace successful! Cost :" + DateUtils.timeCostFrom(start));
    }

    private void renameToCurrentDirect() {
        try {
            BIFileUtils.moveFile(BIPathUtils.createUserTotalTempPath(biUser.getUserId()), BIPathUtils.createUserTotalPath(biUser.getUserId()));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public CubeBuildStuffManager getCubeGeneratingObjects() {
                if (object == null) {
            object = new CubeBuildStuffManager(biUser);
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
     * @return true或fals
     */
    private boolean checkCubePath() {
        return BIFileUtils.checkDir(new File(BIPathUtils.createBasePath()));
    }


}
