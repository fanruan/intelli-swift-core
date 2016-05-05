package com.fr.bi.cal;

import com.fr.bi.cal.generate.index.TempIndexGenerator;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.common.inter.Release;

import com.fr.bi.stable.utils.BIUserUtils;
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
    private TempIndexGenerator loader;
    private TempIndexGenerator loaderGenerating;
    private Queue<TempIndexGenerator> generater;
    private TempCubeTask task;
    private CubeThread cubeThread;
    private Release release;

    public TempCubeManager() {

    }

    public TempCubeManager(TempCubeTask task) {
        this.task = task;
        this.cubeThread = new CubeThread();
        this.generater = new ConcurrentLinkedQueue<TempIndexGenerator>();
        this.cubeThread.start();
    }

    public static void release(TempCubeTask task) {
        if (cubeMap.get(task) != null) {
            cubeMap.get(task).releaseResource();
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

    public TempIndexGenerator getLoader() {
        if (loader == null) {
            loader = loaderGenerating;
        }
        return loader;
    }

    public TempIndexGenerator getLoaderGenerating() {
        return loaderGenerating;
    }

    public boolean addLoader(TempIndexGenerator loader, Release release) {
        this.generater.add(loader);
        synchronized (cubeThread) {
            this.loaderGenerating = loader;
            this.cubeThread.notifyAll();
            if (this.release == null) {
                this.release = release;
            }
        }
        return true;
    }

    @Override
    public void releaseResource() {
        if (loader != null) {
            loader = null;
        }
        if (loaderGenerating != null) {
            loaderGenerating = null;
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
                loaderGenerating = generater.poll();
                loaderGenerating.generateCube();
                loader = loaderGenerating;
                if (release != null) {
                    release.releaseResource();
                }
            }
        }
    }

    ;
}