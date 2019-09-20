package com.fr.swift.executor.task.impl;

import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftSchema;
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
public class TransferExecutorTaskTest {

    String json = "{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":\"test@FINE_IO@0\",\"order\":0}";
    final SegmentKey segmentKey = new SwiftSegmentEntity(new SourceKey("test"), 0, Types.StoreType.FINE_IO, SwiftSchema.CUBE);

    @Test
    public void testSerialize() throws Exception {
        ExecutorTask executorTask = new TransferExecutorTask(segmentKey);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new TransferExecutorTask(new SourceKey("test"), false, SwiftTaskType.TRANSFER, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.currentTimeMillis()), System.currentTimeMillis(), json, 0);
        Assert.assertEquals(executorTask.getJob().serializedTag(), segmentKey);
    }
}
