package com.fr.swift.exception.service;

import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.handler.ExceptionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Marvin
 * @date 8/27/2019
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
public class ExceptionHandlerRegistryTest {

    @Test
    public void testRegisterExceptionHandler() {
        ExceptionHandlerRegistry registry = ExceptionHandlerRegistry.getInstance();

        ExceptionHandler testHandler1 = PowerMockito.mock(ExceptionHandler.class);
        ExceptionHandler testHandler2 = PowerMockito.mock(ExceptionHandler.class);
        ExceptionHandler testHandler3 = PowerMockito.mock(ExceptionHandler.class);
        PowerMockito.when(testHandler1.getExceptionInfoType()).thenReturn(ExceptionInfoType.UPLOAD_SEGMENT);
        PowerMockito.when(testHandler2.getExceptionInfoType()).thenReturn(ExceptionInfoType.DOWNLOAD_SEGMENT);
        PowerMockito.when(testHandler3.getExceptionInfoType()).thenReturn(ExceptionInfoType.CORRUPTED_SEGMENT);

        registry.registerExceptionHandler(testHandler1);
        registry.registerExceptionHandler(testHandler2);
        registry.registerExceptionHandler(testHandler3);

        Assert.assertEquals(testHandler1, registry.getHandler(ExceptionInfoType.UPLOAD_SEGMENT));
        Assert.assertEquals(testHandler2, registry.getHandler(ExceptionInfoType.DOWNLOAD_SEGMENT));
        Assert.assertEquals(testHandler3, registry.getHandler(ExceptionInfoType.CORRUPTED_SEGMENT));
    }
}
