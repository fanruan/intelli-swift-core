package com.fr.swift.exception.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.service.ExceptionInfoService;
import com.fr.swift.service.listener.RemoteSender;
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
@PrepareForTest({BeanFactory.class, SwiftContext.class, ProxySelector.class})
public class ExceptionHandleTemplateTest {

    private ExceptionInfoService infoService;
    private ExceptionHandler handler;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        infoService = PowerMockito.mock(ExceptionInfoService.class);
        PowerMockito.mockStatic(BeanFactory.class);
        BeanFactory factory = PowerMockito.mock(BeanFactory.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(factory);
        PowerMockito.when(factory.getBean(ExceptionInfoService.class)).thenReturn(infoService);

        PowerMockito.mockStatic(ProxySelector.class);
        RemoteSender sender = PowerMockito.mock(RemoteSender.class);
        PowerMockito.when(ProxySelector.getProxy(RemoteSender.class)).thenReturn(sender);
        PowerMockito.when(sender.trigger((SwiftRpcEvent) ArgumentMatchers.any())).thenReturn(null);

        handler = PowerMockito.mock(ExceptionHandler.class);

    }

    @Test
    public void testHandleException() throws Exception {
        ExceptionHandleTemplate template = PowerMockito.spy(new ExceptionHandleTemplate(handler));

        ExceptionInfoBean bean = new ExceptionInfoBean.Builder()
                .setNowAndHere()
                .setContext(PowerMockito.mock(ExceptionContext.class))
                .setType(ExceptionInfoType.UPLOAD_SEGMENT).build();

        PowerMockito.when(handler.handleException(bean)).thenReturn(true);
        template.handleException(bean);
        Mockito.verify(handler).handleException(bean);
        Mockito.verify(infoService).removeExceptionInfo(bean.getId());
        PowerMockito.verifyPrivate(template).invoke("handleExceptionResult", bean, ExceptionInfo.State.SOLVED);

        PowerMockito.when(handler.handleException(bean)).thenReturn(false);
        template.handleException(bean);
        Mockito.verify(handler, Mockito.times(2)).handleException(bean);
        PowerMockito.verifyPrivate(template).invoke("handleExceptionResult", bean, ExceptionInfo.State.UNSOLVED);

    }
}
