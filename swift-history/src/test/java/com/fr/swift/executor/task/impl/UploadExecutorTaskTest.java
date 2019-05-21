package com.fr.swift.executor.task.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.job.impl.UploadJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class UploadExecutorTaskTest {

    String json = "{\"uploadSegmentKey\":{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":\"test@FINE_IO@0\",\"order\":0},\"uploadWholeSeg\":false}";
    SegmentKey segmentKey = new SegmentKeyBean("test", 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);


    @Test
    public void testSerialize() throws Exception {
        UploadExecutorTask executorTask = new UploadExecutorTask(segmentKey, false, null);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new UploadExecutorTask(new SourceKey("test"), false, SwiftTaskType.UPLOAD, LockType.REAL_SEG,
                LockType.REAL_SEG.name(), DBStatusType.ACTIVE, String.valueOf(System.currentTimeMillis()), System.currentTimeMillis(), json);
        UploadJob uploadJob = (UploadJob) executorTask.getJob();

        Field field1 = UploadJob.class.getDeclaredField("uploadSegmentKey");
        Field field2 = UploadJob.class.getDeclaredField("uploadWholeSeg");

        field1.setAccessible(true);
        field2.setAccessible(true);
        SegmentKey uploadSegmentKey = (SegmentKey) field1.get(uploadJob);
        boolean uploadWholeSeg = (Boolean) field2.get(uploadJob);

        Assert.assertEquals(segmentKey, uploadSegmentKey);
        Assert.assertEquals(uploadWholeSeg, false);
    }
}
