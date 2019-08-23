package com.fr.swift.exception.handler;

import com.fr.swift.exception.DownloadExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.segment.SegmentHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Create by lifan on 2019-08-23 10:19
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DownloadExceptionHandler.class, SwiftRepositoryAccessibleTester.class, SegmentHelper.class})
public class DownloadExceptionHandlerTest {

    @Test
    public void handleException() {
        mockStatic(SwiftRepositoryAccessibleTester.class);
        when(SwiftRepositoryAccessibleTester.testAccessible()).thenReturn(true);

        ExceptionInfo exceptionInfo = mock(ExceptionInfo.class);
        DownloadExceptionContext downloadExceptionContext = mock(DownloadExceptionContext.class);
        when(exceptionInfo.getContext()).thenReturn(downloadExceptionContext);

        String sourceKey = mock(String.class);
        Set uris = mock(Set.class);
        when(downloadExceptionContext.getSourceKey()).thenReturn(sourceKey);
        when(downloadExceptionContext.getUris()).thenReturn(uris);
        when(downloadExceptionContext.isReplace()).thenReturn(false);

        Set res = mock(Set.class);
        mockStatic(SegmentHelper.class);
        when(SegmentHelper.download(sourceKey, uris, false)).thenReturn(res);

        DownloadExceptionHandler downloadExceptionHandler = new DownloadExceptionHandler();
        DownloadExceptionHandler mockHandler = spy(downloadExceptionHandler);
        mockHandler.handleException(exceptionInfo);
        verifyStatic(SegmentHelper.class, times(1));

    }

}