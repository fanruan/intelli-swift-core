package com.fr.swift.cloud.executor.task.impl;

import com.fr.swift.cloud.config.entity.SwiftSegmentEntity;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.type.DBStatusType;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.SourceKey;
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
    final SegmentKey segmentKey = new SwiftSegmentEntity(new SourceKey("test"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    @Test
    public void testSerialize() throws Exception {
        ExecutorTask executorTask = new RecoveryExecutorTask(segmentKey);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new RecoveryExecutorTask(new SourceKey("test"), false, SwiftTaskType.RECOVERY, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.currentTimeMillis()), System.currentTimeMillis(), json, 0);
        Assert.assertEquals(executorTask.getJob().serializedTag(), segmentKey);
    }
}
