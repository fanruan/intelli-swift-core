package com.fr.swift.exception.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.handler.ExceptionHandleTemplate;
import com.fr.swift.service.ServiceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * @author Marvin
 * @date 8/26/2019
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, BeanFactory.class, SwiftExceptionHandleService.class})
public class SwiftExceptionHandleServiceTest {

    private ExceptionInfoService infoService;
    private SwiftExceptionHandleService service;
    ExceptionInfoBean bean;

    @Before
    public void setUp() throws Exception {
        infoService = PowerMockito.mock(ExceptionInfoService.class);
        service = PowerMockito.spy(new SwiftExceptionHandleService());
        Whitebox.setInternalState(service, "infoService", infoService);

        bean = new ExceptionInfoBean.Builder()
                .setNowAndHere()
                .setContext(PowerMockito.mock(ExceptionContext.class))
                .setType(ExceptionInfoType.UPLOAD_SEGMENT).build();
        PowerMockito.when(infoService.removeExceptionInfo(bean.getId())).thenReturn(true);
        PowerMockito.when(infoService.maintain(bean)).thenReturn(true);
        ExceptionHandleTemplate template = PowerMockito.mock(ExceptionHandleTemplate.class);
        PowerMockito.whenNew(ExceptionHandleTemplate.class).withAnyArguments().thenReturn(template);
        PowerMockito.doNothing().when(template).handleException((ExceptionInfo) ArgumentMatchers.any());
    }

    @Test
    public void testHandleException() throws Exception {
        service.handleException(bean);
        PowerMockito.verifyPrivate(service).invoke("updateExceptionInfo", bean);
        Assert.assertEquals(ServiceType.EXCEPTION, service.getServiceType());
    }
}
