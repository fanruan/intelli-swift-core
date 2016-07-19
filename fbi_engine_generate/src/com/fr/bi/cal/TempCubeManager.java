package com.fr.bi.cal;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.impl.conf.CubeBuildTableSource;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 实时报表cube管理
 *
 * @author guy
 */
public class TempCubeManager implements Release {

    private static Map<TempCubeTask, TempCubeManager> cubeMap = new ConcurrentHashMap<TempCubeTask, TempCubeManager>();
    private CubeBuildTableSource cubeBuildTool;
    private CubeBuildTableSource cubeBuildToolGenerating;
    private Queue<CubeBuildTableSource> generater;
    private TempCubeTask task;
    private CubeThread cubeThread;
    private Release release;

    public TempCubeManager() {

    }

    public TempCubeManager(TempCubeTask task) {
        this.task = task;
        this.cubeThread = new CubeThread();
        this.generater = new ConcurrentLinkedQueue<CubeBuildTableSource>();
        this.cubeThread.start();
    }

    public static void release(TempCubeTask task) {
        if (cubeMap.get(task) != null) {
            cubeMap.get(task).clear();
        }
        cubeMap.remove(task);
    }

    public static TempCubeManager getInstance(TempCubeTask task) {
        synchronized (TempCubeManager.class) {
            Long key = task.getUserId();
            boolean useAdministrtor = BIUserUtils.isAdministrator(task.getUserId());
            if (useAdministrtor) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            task.setUserId(key);
            TempCubeManager manager = cubeMap.get(task);
            if (manager == null) {
                manager = new TempCubeManager(task);
                cubeMap.put(task, manager);
            }
            return manager;
        }
    }

    public CubeBuildTableSource getCubeBuildTool() {
        if (cubeBuildTool == null) {
            cubeBuildTool = cubeBuildToolGenerating;
        }
        return cubeBuildTool;
    }

    public CubeBuildTableSource getCubeBuildToolGenerating() {
        return cubeBuildToolGenerating;
    }

    public boolean addLoader(CubeBuildTableSource cubeBuildTool, Release release) {
        this.generater.add(cubeBuildTool);
        synchronized (cubeThread) {
            this.cubeBuildToolGenerating = cubeBuildTool;
            this.cubeThread.notifyAll();
            if (this.release == null) {
                this.release = release;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        if (cubeBuildTool != null) {
            cubeBuildTool = null;
        }
        if (cubeBuildToolGenerating != null) {
            cubeBuildToolGenerating = null;
        }
        if (cubeThread != null) {
            cubeThread = null;
        }
    }

    private class CubeThread extends Thread {

        private volatile boolean stop = false;

        private CubeThread() {
        }

        @Override
        public void run() {
            p:
            while (!stop) {
                if (generater.size() == 0) {
                    synchronized (this) {
                        try {
                            if (generater.isEmpty()) {
                                this.wait();
                            }
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                cubeBuildToolGenerating = generater.poll();
                BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
                CubeTask cubeGenerateTask = new BuildCubeTask(new BIUser(task.getUserId()), cubeBuildToolGenerating);
                cubeManager.addTask(cubeGenerateTask, task.getUserId());
                cubeBuildTool = cubeBuildToolGenerating;
                while (cubeManager.hasTask(cubeGenerateTask, task.getUserId())) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        BILogger.getLogger().error(e.getMessage());
                    }
                }
                if (release != null) {
                    release.clear();
                }
            }
        }
    }
}
