package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.job.impl.RealtimeInsertJob;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/3/13
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RealtimeInsertExecutorTask.class})
public class RealtimeInsertExecutorTaskTest {
    @Test
    public void test() throws Exception {
        SourceKey tableKey = mock(SourceKey.class);
        SwiftResultSet resultSet = mock(SwiftResultSet.class);

        RealtimeInsertJob job = mock(RealtimeInsertJob.class);
        whenNew(RealtimeInsertJob.class).withArguments(tableKey, resultSet).thenReturn(job);

        RealtimeInsertExecutorTask executorTask = new RealtimeInsertExecutorTask(tableKey, resultSet);

        assertEquals(tableKey, executorTask.getSourceKey());
        assertFalse(executorTask.isPersistent());
        assertEquals(ExecutorTaskType.REALTIME, executorTask.getExecutorTaskType());
        assertEquals(LockType.VIRTUAL_SEG, executorTask.getLockType());
        assertEquals(Strings.EMPTY, executorTask.getLockKey());
        assertEquals(job, executorTask.getJob());
    }
}