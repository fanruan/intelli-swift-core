package com.fr.swift.executor.task.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class CollateExecutorTaskTest {

    String json = "[{\"sourceKey\":\"test\",\"storeType\":\"FINE_IO\",\"swiftSchema\":\"CUBE\",\"id\":" +
            "\"test@FINE_IO@0\",\"order\":0},{\"sourceKey\":\"test\",\"storeType\":\"MEMORY\"" +
            ",\"swiftSchema\":\"CUBE\",\"id\":\"test@MEMORY@1\",\"order\":1}]";

    String json2 = "[\"test@FINE_IO@2\",\"test@FINE_IO@3\"]";

    final SegmentKey segmentKey1 = new SwiftSegmentEntity(new SourceKey("test"), 0, Types.StoreType.FINE_IO, SwiftSchema.CUBE);
    final SegmentKey segmentKey2 = new SwiftSegmentEntity(new SourceKey("test"), 1, Types.StoreType.MEMORY, SwiftSchema.CUBE);

    final SegmentKey segmentKey3 = new SwiftSegmentEntity(new SourceKey("test"), 2, Types.StoreType.FINE_IO, SwiftSchema.CUBE);
    final SegmentKey segmentKey4 = new SwiftSegmentEntity(new SourceKey("test"), 3, Types.StoreType.FINE_IO, SwiftSchema.CUBE);

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        // mock segment service
        SwiftSegmentService swiftSegmentService = mock(SwiftSegmentService.class);
        when(beanFactory.getBean("segmentServiceProvider", SwiftSegmentService.class)).thenReturn(swiftSegmentService);
        when(swiftSegmentService.getByIds(new HashSet<String>() {{
            add("test@FINE_IO@2");
            add("test@FINE_IO@3");
        }})).thenReturn(new HashSet<SegmentKey>() {{
            add(segmentKey3);
            add(segmentKey4);
        }});
    }

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
                "test", DBStatusType.ACTIVE, String.valueOf(System.currentTimeMillis()), System.currentTimeMillis(), json2, 0);
        List<SegmentKey> list = (List<SegmentKey>) executorTask.getJob().serializedTag();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(list.get(0), "test@FINE_IO@2");
        Assert.assertEquals(list.get(1), "test@FINE_IO@3");
    }
}
