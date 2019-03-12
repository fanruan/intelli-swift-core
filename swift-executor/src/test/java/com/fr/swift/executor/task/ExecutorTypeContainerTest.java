package com.fr.swift.executor.task;

import com.fr.swift.executor.type.ExecutorTaskType;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class ExecutorTypeContainerTest {

    @Test
    public void test() {
        ExecutorTypeContainer.getInstance().registerClass(ExecutorTaskType.COLLATE, AbstractExecutorTask.class);
        Assert.assertEquals(ExecutorTypeContainer.getInstance().getClassByType(ExecutorTaskType.COLLATE), AbstractExecutorTask.class);
    }
}
