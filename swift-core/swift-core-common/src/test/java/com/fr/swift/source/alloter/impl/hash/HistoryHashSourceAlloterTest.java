package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class HistoryHashSourceAlloterTest {
    @Mock
    SwiftSegmentBucketService bucketService;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftSegmentService swiftSegmentService = mock(SwiftSegmentService.class);
        when(beanFactory.getBean("segmentServiceProvider", SwiftSegmentService.class)).thenReturn(swiftSegmentService);
        when(beanFactory.getBean(SwiftSegmentBucketService.class)).thenReturn(bucketService);

        when(swiftSegmentService.tryAppendSegment(ArgumentMatchers.<SourceKey>any(), ArgumentMatchers.<Types.StoreType>any())).thenAnswer(new Answer<SegmentKey>() {
            int order = 0;

            @Override
            public SegmentKey answer(InvocationOnMock invocation) throws Throwable {
                SourceKey tableKey = invocation.getArgument(0);
                Types.StoreType storeType = invocation.getArgument(1);
                return new SwiftSegmentEntity(tableKey, order++, storeType, null);
            }
        });
    }

    @Test
    public void testAllot() {
        HistoryHashSourceAlloter alloter = new HistoryHashSourceAlloter(new SourceKey("a"), new HashAllotRule());

        HashRowInfo hashRowInfo1 = new HashRowInfo(new ListBasedRow(0));
        HashRowInfo hashRowInfo2 = new HashRowInfo(new ListBasedRow(1));
        HashRowInfo hashRowInfo3 = new HashRowInfo(new ListBasedRow(2));
        HashRowInfo hashRowInfo4 = new HashRowInfo(new ListBasedRow(3));
        HashRowInfo hashRowInfo5 = new HashRowInfo(new ListBasedRow(4));
        Assert.assertEquals(newSegInfo(0), alloter.allot(hashRowInfo1));
        Assert.assertEquals(newSegInfo(1), alloter.allot(hashRowInfo2));
        Assert.assertEquals(newSegInfo(2), alloter.allot(hashRowInfo3));
        Assert.assertEquals(newSegInfo(3), alloter.allot(hashRowInfo4));
        Assert.assertEquals(newSegInfo(4), alloter.allot(hashRowInfo5));
        Mockito.verify(bucketService, Mockito.times(5)).saveElement(Mockito.any(SwiftSegmentBucketElement.class));

    }

    protected SegmentInfo newSegInfo(int order) {
        return new SwiftSegmentInfo(order, Types.StoreType.FINE_IO);
    }

}
