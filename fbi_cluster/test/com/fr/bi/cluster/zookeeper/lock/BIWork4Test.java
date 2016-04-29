package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;

/**
 * Created by Connery on 2015/3/28.
 */
public class BIWork4Test extends BIWorker {
    private long id = 0;

    @Override
    protected void dealTask(BIWorkerNodeValue biWorkerTask) {
        System.out.println(biWorkerTask.getMissionID());
//        System.out.println(biWorkerTask.getTaskName());
//        System.out.println(biWorkerTask.getTaskContent());
        System.out.println(biWorkerTask.getStatus());
        if (id != biWorkerTask.getMissionID()) {
            id = biWorkerTask.getMissionID();
            simulate(biWorkerTask);
        }
    }

    private void simulate(BIWorkerNodeValue biWorkerTask) {
        try {
//            System.out.println("当前线程为："+Thread.currentThread().getName());
            Thread.sleep(2000);
            byte[] con = zk.getData(getFocusedEventPath(), false, null);
            BIWorkerNodeValue temp = new BIWorkerNodeValue();
            temp.init(con);
            System.out.println(Thread.currentThread().getName());
//            System.out.println("当前线程为："+Thread.currentThread().getId());
//            System.out.println("当前任务为：："+temp.getMissionID());
//            System.out.println("处理任务为：："+biWorkerTask.getMissionID());
            if (temp.getMissionID() != biWorkerTask.getMissionID()) {
//                System.out.println("任务变更为："+temp.getMissionID()+"，返回");
                return;
            }
//            System.out.println(getFocusedEventPath()+"接受任务");
            biWorkerTask.setStatus(BIWorkerNodeValue.STATUS_ACCEPT);
            zk.setData(getFocusedEventPath(), biWorkerTask.toByte(), -1);
            Thread.sleep(2000);
//            System.out.println("当前线程为："+Thread.currentThread().getId());
//            System.out.println("当前任务为：："+temp.getMissionID());
//            System.out.println("处理任务为：："+biWorkerTask.getMissionID());
            con = zk.getData(getFocusedEventPath(), false, null);
            temp.init(con);
            if (temp.getMissionID() != biWorkerTask.getMissionID()) {
//                System.out.println("任务变更为："+temp.getMissionID()+"，返回");
                return;
            }
//            System.out.println(getFocusedEventPath()+"完成任务");
            biWorkerTask.setStatus(BIWorkerNodeValue.STATUS_FINISH);
            zk.setData(getFocusedEventPath(), biWorkerTask.toByte(), -1);

        } catch (Exception ex) {

        }
    }
}