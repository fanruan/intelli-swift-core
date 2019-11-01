package com.fr.swift.exception.handler;

import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.PushSegmentExceptionContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MasterPushSegExceptionHandler.class)
public class MasterPushSegExceptionHandlerTest {

    @Test
    public void handleException() throws Exception {
        ExceptionInfo exceptionInfo = mock(ExceptionInfo.class);
        PushSegmentExceptionContext pushSegmentExceptionContext = mock(PushSegmentExceptionContext.class);
        when(exceptionInfo.getContext()).thenReturn(pushSegmentExceptionContext);

        MasterPushSegExceptionHandler masterPushSegExceptionHandler = mock(MasterPushSegExceptionHandler.class);
        when(masterPushSegExceptionHandler, "retryPush", pushSegmentExceptionContext).thenReturn(true);
        Assert.assertEquals(false, masterPushSegExceptionHandler.handleException(exceptionInfo));
    }
}