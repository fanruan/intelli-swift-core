package com.fr.swift.task;

/**
 * @author anchore
 * @date 2018/1/12
 * <p>
 * 本地任务，既是SchedulerTask又是WorkerTask
 * 有些任务非常适合捆绑在一起做，比如取数之后再做索引，本机就可以通知到，不用走rpc
 */
public interface LocalTask extends SchedulerTask, WorkerTask {
}
