package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class TruncateExecutorTaskTest {

    String json = "{\"id\":\"test\"}";

    @Test
    public void testSerialize() throws Exception {
        ExecutorTask executorTask = new TruncateExecutorTask(new SourceKey("test"));
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new TruncateExecutorTask(new SourceKey("test"), false, ExecutorTaskType.TRUNCATE, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.nanoTime()), System.nanoTime(), json);
        Assert.assertEquals(executorTask.getJob().serializedTag(), new SourceKey("test"));
    }
}
