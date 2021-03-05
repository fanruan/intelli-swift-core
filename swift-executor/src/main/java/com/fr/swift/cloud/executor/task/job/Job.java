package com.fr.swift.cloud.executor.task.job;

import java.util.concurrent.Callable;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public interface Job<T, P> extends Callable<T> {

    JobListener getJobListener();

    void setJobListener(JobListener listener);
    /**
     * @author anchore
     * @date 2019/2/27
     */
    interface JobListener<C> {
        void onDone(boolean success);

        void callback(C callback) throws Exception;
    }

    P serializedTag();
}
