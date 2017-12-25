package com.finebi.base.context;

import com.finebi.springframework.beans.factory.support.RootBeanDefinition;
import com.finebi.springframework.web.context.WebApplicationContext;
import com.finebi.springframework.web.servlet.DispatcherServlet;
import com.finebi.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import com.finebi.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.finebi.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by andrew_asa on 2017/12/24.
 */
public class BaseDispatcherServlet extends DispatcherServlet {

    // 只是记录action的包就行了，其它类的管理交个BaseContext来进行管理
    public static String [] scranPath = {
            "com.finebi.web.conf.action",
            "com.finebi.web.conf.exception",
    };

    @Override
    protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {

        BaseWebAppContext wac = new BaseWebAppContext();
        //wac.setParent(StableManager.getContext());
        wac.addScanPath(scranPath);
        wac.registerBeanDefinition("exceptionResolver", new RootBeanDefinition(ExceptionHandlerExceptionResolver.class));
        wac.registerBeanDefinition("RequestMappingHandlerMapping", new RootBeanDefinition(RequestMappingHandlerMapping.class));
        wac.registerBeanDefinition("RequestMappingHandlerAdapter", new RootBeanDefinition(RequestMappingHandlerAdapter.class));
        wac.refresh();
        //wac.refresh();
        return (WebApplicationContext) wac;
    }
}
