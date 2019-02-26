package com.fr.swift.executor.type;

import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * This class created on 2019/2/22
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class LockTypeTest {

    @Mock
    ExecutorTask executorTask1;
    @Mock
    ExecutorTask executorTask2;
    @Mock
    ExecutorTask executorTask3;
    @Mock
    ExecutorTask executorTask4;
    @Mock
    ExecutorTask executorTask5;


    @Before
    public void setUp() throws Exception {
        Mockito.when(executorTask1.getLockType()).thenReturn(LockType.TABLE);
        Mockito.when(executorTask2.getLockType()).thenReturn(LockType.VIRTUAL_SEG);
        Mockito.when(executorTask3.getLockType()).thenReturn(LockType.NONE);
        Mockito.when(executorTask4.getLockType()).thenReturn(LockType.REAL_SEG);

        Mockito.when(executorTask4.getLockKey()).thenReturn("seg0");
        Mockito.when(executorTask5.getLockKey()).thenReturn("seg1");

        Mockito.when(executorTask1.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTask2.getSourceKey()).thenReturn(new SourceKey("A"));
        Mockito.when(executorTask3.getSourceKey()).thenReturn(new SourceKey("B"));
    }

    @Test
    public void sameTableTest() {
        Assert.assertTrue(LockType.isSameTable(executorTask1, executorTask2));
        Assert.assertFalse(LockType.isSameTable(executorTask1, executorTask3));
    }

    @Test
    public void tableTest() {
        Assert.assertTrue(LockType.isTableLock(executorTask1));
        Assert.assertFalse(LockType.isTableLock(executorTask2));
    }

    @Test
    public void realSegTest() {
        Assert.assertTrue(LockType.isRealLock(executorTask4));
        Assert.assertFalse(LockType.isRealLock(executorTask3));
    }

    @Test
    public void vitualSegTest() {
        Assert.assertTrue(LockType.isVirtualLock(executorTask2));
        Assert.assertFalse(LockType.isVirtualLock(executorTask3));
    }

    @Test
    public void noneSegTest() {
        Assert.assertTrue(LockType.isNoneLock(executorTask3));
        Assert.assertFalse(LockType.isNoneLock(executorTask4));
    }

    @Test
    public void lockKeySameTest() {
        Assert.assertTrue(LockType.isSameLockKey(executorTask4, executorTask4));
        Assert.assertFalse(LockType.isSameLockKey(executorTask4, executorTask5));
    }

}
