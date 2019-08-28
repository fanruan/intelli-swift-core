package com.fr.swift.exception.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.UploadExceptionContext;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.SwiftUploadService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

/**
 * @author Marvin
 * @date 8/23/2019
 * @description
 * @since swift 1.1
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftRepositoryAccessibleTester.class, SwiftContext.class, BeanFactory.class})
public class UploadSegExceptionHandlerTest {

    @Test
    public void testHandleException() throws Exception {

        PowerMockito.mockStatic(SwiftContext.class);
        SwiftUploadService service = PowerMockito.mock(SwiftUploadService.class);
        BeanFactory factory = PowerMockito.mock(BeanFactory.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(factory);
        PowerMockito.when(factory.getBean(SwiftUploadService.class)).thenReturn(service);

        PowerMockito.mockStatic(SwiftRepositoryAccessibleTester.class);
        PowerMockito.when(SwiftRepositoryAccessibleTester.testAccessible()).thenReturn(true);

        ExceptionInfo info = PowerMockito.mock(ExceptionInfo.class);
        UploadExceptionContext context = PowerMockito.mock(UploadExceptionContext.class);
        PowerMockito.when(info.getContext()).thenReturn(context);

        UploadSegExceptionHandler handler = PowerMockito.spy(new UploadSegExceptionHandler());

        PowerMockito.when(context.isAllShow()).thenReturn(true);
        Assert.assertEquals(true, handler.handleException(info));
        Mockito.verify(service).uploadAllShow(Collections.singleton((SegmentKey) context.getSegmentKey()));

        PowerMockito.when(context.isAllShow()).thenReturn(false);
        Assert.assertEquals(true, handler.handleException(info));
        Mockito.verify(service).upload(Collections.singleton((SegmentKey) context.getSegmentKey()));

        Assert.assertEquals(ExceptionInfoType.UPLOAD_SEGMENT,handler.getExceptionInfoType());
    }
}
