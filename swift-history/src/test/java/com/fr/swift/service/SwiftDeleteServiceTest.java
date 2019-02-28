package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.db.Where;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/24
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class SwiftDeleteServiceTest {
    @Mock
    private SwiftSegmentManager segmentManager;

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);

        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        when(beanFactory.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(segmentManager);
    }

    @Test
    public void start() throws SwiftServiceException {
        SwiftDeleteService deleteService = new SwiftDeleteService();

        assertTrue(deleteService.start());
    }

    @Test
    public void shutdown() throws SwiftServiceException {
        SwiftDeleteService deleteService = new SwiftDeleteService();
        deleteService.start();

        assertTrue(deleteService.shutdown());
        assertNull(Whitebox.getInternalState(deleteService, SwiftSegmentManager.class));
    }

    @Test
    public void delete() throws Exception {
        SwiftDeleteService deleteService = new SwiftDeleteService();
        deleteService.start();

        Where where = mock(Where.class);
        SourceKey tableKey = new SourceKey("t");

        List<SegmentKey> segKeys = Arrays.asList(mock(SegmentKey.class), mock(SegmentKey.class), mock(SegmentKey.class));
        when(segmentManager.getSegmentKeys(tableKey)).thenReturn(segKeys);
        when(segmentManager.existsSegment(ArgumentMatchers.<SegmentKey>any())).thenReturn(false, true, true);

        WhereDeleter whereDeleter = mock(WhereDeleter.class);
        when(SwiftContext.get().getBean(eq("decrementer"), any())).thenReturn(whereDeleter);

        // 删除失败的情况
        when(whereDeleter.delete(ArgumentMatchers.<Where>any())).thenThrow(Exception.class).thenReturn(null);
        assertFalse(deleteService.delete(tableKey, where));

        verify(SwiftContext.get(), never()).getBean("decrementer", segKeys.get(0));
        verify(SwiftContext.get()).getBean("decrementer", segKeys.get(1));
        verify(SwiftContext.get()).getBean("decrementer", segKeys.get(2));

        verify(whereDeleter, times(2)).delete(where);

        verifyNoMoreInteractions(whereDeleter);
    }

    @Test
    public void getServiceType() {
        assertEquals(ServiceType.DELETE, new SwiftDeleteService().getServiceType());
    }
}