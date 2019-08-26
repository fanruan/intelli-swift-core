package com.fr.swift.exception.handler;

import com.fr.swift.exception.DownloadExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.segment.SegmentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Create by lifan on 2019-08-23 10:19
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DownloadExceptionHandler.class, SwiftRepositoryAccessibleTester.class, SegmentHelper.class})
public class DownloadExceptionHandlerTest {

    @Test
    public void handleException() throws Exception {
        mockStatic(SwiftRepositoryAccessibleTester.class);
        when(SwiftRepositoryAccessibleTester.testAccessible()).thenReturn(true);

        ExceptionInfo exceptionInfo = mock(ExceptionInfo.class);
        DownloadExceptionContext downloadExceptionContext = mock(DownloadExceptionContext.class);
        when(exceptionInfo.getContext()).thenReturn(downloadExceptionContext);

        DownloadExceptionHandler downloadExceptionHandler = mock(DownloadExceptionHandler.class);
        when(downloadExceptionHandler, "retryDownload", downloadExceptionContext).thenReturn(true);
        Assert.assertEquals(false, downloadExceptionHandler.handleException(exceptionInfo));

    }

}