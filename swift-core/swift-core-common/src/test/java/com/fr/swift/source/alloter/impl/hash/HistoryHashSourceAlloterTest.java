package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.source.alloter.impl.hash.function.TimeHashFunction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftSegmentService swiftSegmentService = mock(SwiftSegmentService.class);
        when(beanFactory.getBean("segmentServiceProvider", SwiftSegmentService.class)).thenReturn(swiftSegmentService);

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

        TimeHashFunction timeHashFunction = new TimeHashFunction();
        HistoryHashSourceAlloter alloter2 = new HistoryHashSourceAlloter(new SourceKey("b"), new HashAllotRule(new int[]{0}, timeHashFunction));

        HashRowInfo hashRowInfo1 = new HashRowInfo(new ListBasedRow(0));
        HashRowInfo hashRowInfo2 = new HashRowInfo(new ListBasedRow(1));
        HashRowInfo hashRowInfo3 = new HashRowInfo(new ListBasedRow(2));
        HashRowInfo hashRowInfo4 = new HashRowInfo(new ListBasedRow(3));
        HashRowInfo hashRowInfo5 = new HashRowInfo(new ListBasedRow(4));
        HashRowInfo hashRowInfo6 = new HashRowInfo(new ListBasedRow(1262361600000L));
        HashRowInfo hashRowInfo7 = new HashRowInfo(new ListBasedRow(1265040000000l));
        HashRowInfo hashRowInfo8 = new HashRowInfo(new ListBasedRow(1269187200000L));
        HashRowInfo hashRowInfo9 = new HashRowInfo(new ListBasedRow(1271692800000L));

        Assert.assertEquals(newSegInfo(0), alloter.allot(hashRowInfo1));
        Assert.assertEquals(newSegInfo(1), alloter.allot(hashRowInfo2));
        Assert.assertEquals(newSegInfo(2), alloter.allot(hashRowInfo3));
        Assert.assertEquals(newSegInfo(3), alloter.allot(hashRowInfo4));
        Assert.assertEquals(newSegInfo(4), alloter.allot(hashRowInfo5));
        Assert.assertEquals(newSegInfo(5), alloter2.allot(hashRowInfo6));
        Assert.assertEquals(newSegInfo(6), alloter2.allot(hashRowInfo7));
        Assert.assertEquals(newSegInfo(7), alloter2.allot(hashRowInfo8));
        Assert.assertEquals(newSegInfo(8), alloter2.allot(hashRowInfo9));
//        Mockito.verify(bucketService, Mockito.times(9)).save(Mockito.any(SwiftSegmentBucketElement.class));


    }

    protected SegmentInfo newSegInfo(int order) {
        return new SwiftSegmentInfo(order, Types.StoreType.FINE_IO);
    }

}
