package com.fr.swift.executor.task.impl;

import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.job.impl.DownloadJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
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
public class DownloadExecutorTastTest {

    String json = "{\"downloadWholeSeg\":false,\"downloadSegKey\":{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":\"test@FINE_IO@0\",\"order\":0},\"replace\":true}";
    SegmentKey segmentKey = new SwiftSegmentEntity(new SourceKey("test"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    @Test
    public void testSerialize() throws Exception {
        DownloadExecutorTask executorTask = new DownloadExecutorTask(segmentKey, false, true);
        Assert.assertEquals(json, executorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new DownloadExecutorTask(new SourceKey("test"), false, ExecutorTaskType.DOWNLOAD, LockType.REAL_SEG,
                LockType.REAL_SEG.name(), DBStatusType.ACTIVE, String.valueOf(System.nanoTime()), System.nanoTime(), json);
        DownloadJob downloadJob = (DownloadJob) executorTask.getJob();

        Field field1 = DownloadJob.class.getDeclaredField("downloadSegKey");
        Field field2 = DownloadJob.class.getDeclaredField("downloadWholeSeg");
        Field field3 = DownloadJob.class.getDeclaredField("replace");

        field1.setAccessible(true);
        field2.setAccessible(true);
        field3.setAccessible(true);
        SegmentKey jobSegmentKey = (SegmentKey) field1.get(downloadJob);
        boolean downloadWholeSeg = (Boolean) field2.get(downloadJob);
        boolean replace = (Boolean) field3.get(downloadJob);

        Assert.assertEquals(segmentKey, jobSegmentKey);
        Assert.assertEquals(downloadWholeSeg, false);
        Assert.assertEquals(replace, true);
    }
}
