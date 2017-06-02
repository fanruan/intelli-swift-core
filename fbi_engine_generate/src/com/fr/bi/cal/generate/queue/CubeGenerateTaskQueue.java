package com.fr.bi.cal.generate.queue;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.cal.generate.task.CustomCubeGenerateTask;
import com.finebi.cube.conf.ICubeGenerateTask;
import com.fr.bi.stable.structure.queue.CubeTaskCondition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Lucifer on 2017-5-19.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 * 不做成单例，让cubeManager单独维护防止外部调用，直接操作队列中的task
 */
public class CubeGenerateTaskQueue {

    private String threadName = "CubeTaskConditionSignalThread";

    private BlockingQueue<ICubeGenerateTask> queue = new LinkedBlockingQueue<ICubeGenerateTask>(
            1000);
    private Object lockObject = new Object();

//    private static CubeGenerateTaskQueue instance;
//
//    static {
//        instance = new CubeGenerateTaskQueue();
//    }

    public CubeGenerateTaskQueue() {
        new Thread(new Runnable() {
            private long sleepTime = 10000l;

            /**
             * 当CubeRunner hasTask()为false时。确保做了signalAll操作。
             */
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!CubeGenerationManager.getCubeManager().hasTask()) {
                            CubeTaskCondition.getInstance().signalAll();
                        }
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        BILoggerFactory.getLogger(CubeTaskCondition.class).error(e.getMessage(), e);
                    }
                }
            }
        }, threadName).start();
    }

//    public static CubeGenerateTaskQueue getInstance() {
//        return instance;
//    }

    public void put(ICubeGenerateTask tableTask) throws InterruptedException {
        synchronized (lockObject) {
            queue.put(tableTask);
        }
    }

    public ICubeGenerateTask poll() throws InterruptedException {
        synchronized (lockObject) {
            return queue.poll();
        }
    }

    public ICubeGenerateTask take() throws InterruptedException {
        return queue.take();
    }

    public ICubeGenerateTask peek() {
        return queue.peek();
    }

    public boolean remove(ICubeGenerateTask task) {
        synchronized (lockObject) {
            return queue.remove(task);
        }
    }

    public void showTasks() {
        synchronized (lockObject) {
            BILoggerFactory.getLogger(CubeGenerateTaskQueue.class).info("TaskList info:");
            for (ICubeGenerateTask iCubeGenerateTask : getCubeGenerateTasks()) {
                BILoggerFactory.getLogger(CubeGenerateTaskQueue.class).info(iCubeGenerateTask.getTaskInfo());
            }
        }
    }

    /**
     * 获取任务队列的所有任务(有延迟)
     *
     * @return
     */
    public List<ICubeGenerateTask> getCubeGenerateTasks() {
        synchronized (lockObject) {
            Iterator<ICubeGenerateTask> iterator = queue.iterator();
            List<ICubeGenerateTask> cubeGenerateTaskList = new ArrayList<ICubeGenerateTask>();
            while (iterator.hasNext()) {
                cubeGenerateTaskList.add(iterator.next());
            }
            return cubeGenerateTaskList;
        }
    }

    public boolean isEmpty() {
        synchronized (lockObject) {
            return queue.isEmpty();
        }
    }

    public int getSize() {
        synchronized (lockObject) {
            return queue.size();
        }
    }

    /**
     * 获取所有队列中过的表ID
     *
     * @return
     */
    public List<String> getTableSourceIds() {
        synchronized (lockObject) {
            Iterator<ICubeGenerateTask> iterator = queue.iterator();
            List<String> tableSourceIdList = new ArrayList<String>();
            while (iterator.hasNext()) {
                ICubeGenerateTask cubeGenerateTask = iterator.next();
                if (cubeGenerateTask.getTableSourceId() != null) {
                    tableSourceIdList.add(cubeGenerateTask.getTableSourceId());
                }
                if (cubeGenerateTask instanceof CustomCubeGenerateTask) {
                    tableSourceIdList.addAll(((CustomCubeGenerateTask) cubeGenerateTask).getSourceIdUpdateTypeMap().keySet());
                }
            }
            return tableSourceIdList;
        }
    }
}
