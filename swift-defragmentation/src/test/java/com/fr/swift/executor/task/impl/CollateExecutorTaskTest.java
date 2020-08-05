package com.fr.swift.executor.task.impl;

import com.fr.swift.config.entity.SwiftSegmentEntity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class CollateExecutorTaskTest {

    String json = "[{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":" +
            "\"test@FINE_IO@0\",\"order\":0},{\"sourceKey\":\"test\",\"storeType\":\"MEMORY\"" +
            ",\"swiftSchema\":\"CUBE\",\"id\":\"test@MEMORY@1\",\"order\":1}]";

    final SegmentKey segmentKey1 = new SwiftSegmentEntity(new SourceKey("test"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    final SegmentKey segmentKey2 = new SwiftSegmentEntity(new SourceKey("test"), 1, Types.StoreType.MEMORY, SwiftDatabase.CUBE);

    @Test
    public void testSerialize() throws Exception {
        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>() {{
            add(segmentKey1);
            add(segmentKey2);
        }};
        ExecutorTask executorTask = new CollateExecutorTask(new SourceKey("test"), segmentKeyList);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        final ExecutorTask executorTask = new CollateExecutorTask(new SourceKey("test"), false, SwiftTaskType.COLLATE, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.currentTimeMillis()), System.currentTimeMillis(), json, 0);
        List<SegmentKey> list = (List<SegmentKey>) executorTask.getJob().serializedTag();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0), segmentKey1);
        Assert.assertEquals(list.get(1), segmentKey2);
    }
}
