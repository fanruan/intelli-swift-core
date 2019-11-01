package com.fr.swift.exception.reporter;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.queue.SlaveExceptionInfoQueue;
import com.fr.swift.exception.service.ExceptionInfoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Marvin
 * @date 8/26/2019
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BeanFactory.class, SwiftContext.class, ExceptionReporter.class, SlaveExceptionInfoQueue.class})
public class ExceptionReportTest {

    ExceptionInfoService infoService;
    SlaveExceptionInfoQueue queue;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(SwiftContext.class);
        infoService = PowerMockito.mock(ExceptionInfoService.class);
        PowerMockito.mockStatic(BeanFactory.class);
        BeanFactory factory = PowerMockito.mock(BeanFactory.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(factory);
        PowerMockito.when(factory.getBean(ExceptionInfoService.class)).thenReturn(infoService);

        PowerMockito.mockStatic(ExceptionReporter.class);

        PowerMockito.mockStatic(SlaveExceptionInfoQueue.class);
        queue = PowerMockito.mock(SlaveExceptionInfoQueue.class);
        PowerMockito.when(SlaveExceptionInfoQueue.getInstance()).thenReturn(queue);
    }

    @Test
    public void testReport() {
        PowerMockito.spy(ExceptionReporter.class);
        ExceptionContext context = PowerMockito.mock(ExceptionContext.class);
        PowerMockito.when(infoService.existsException((ExceptionInfo) ArgumentMatchers.any())).thenReturn(false);
        SwiftContext.get().getBean(ExceptionReporter.class).report(context, ExceptionInfoType.UPLOAD_SEGMENT);
        Mockito.verify(queue).offer((ExceptionInfo) ArgumentMatchers.any());
    }
}
