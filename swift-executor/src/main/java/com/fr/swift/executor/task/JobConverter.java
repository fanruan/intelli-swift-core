package com.fr.swift.executor.task;

import com.fr.swift.executor.task.job.Job;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public interface JobConverter {
    Job convert2Callable();
}
