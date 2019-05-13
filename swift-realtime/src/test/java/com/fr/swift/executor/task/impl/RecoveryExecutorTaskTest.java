package com.fr.swift.executor.task.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class RecoveryExecutorTaskTest {

    String json = "{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":\"test@FINE_IO@0\",\"order\":0}";
    final SegmentKey segmentKey = new SegmentKeyBean("test", 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    @Test
    public void testSerialize() throws Exception {
        ExecutorTask executorTask = new RecoveryExecutorTask(segmentKey);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new RecoveryExecutorTask(new SourceKey("test"), false, SwiftTaskType.RECOVERY, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.nanoTime()), System.nanoTime(), json);
        Assert.assertEquals(executorTask.getJob().serializedTag(), segmentKey);
    }
}
