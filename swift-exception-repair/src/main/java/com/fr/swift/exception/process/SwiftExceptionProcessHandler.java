package com.fr.swift.exception.process;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.ExceptionProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.service.ExceptionHandleService;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;

/**
 * @author Marvin
 * @date 8/9/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(ExceptionProcessHandler.class)
public class SwiftExceptionProcessHandler extends AbstractProcessHandler<URL> implements ExceptionProcessHandler {
    public SwiftExceptionProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        OperateClusterSelector selector = DummyClusterSelector.getInstance();
        String clusterId = selector.selectCluster((ExceptionInfo) args[0]);
        UrlFactory factory = UrlSelector.getInstance().getFactory();
        URL url = factory.getURL(clusterId);
        return url;
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        try {
            Class proxy = method.getDeclaringClass();
            String methodName = method.getName();
            Class[] paramClass = method.getParameterTypes();
            URL url = processUrl(targets, args);
            //url为null时invokerCreator.createAsyncInvoker会调用本地的方法，可能出现死循环，判断url是否为null来避免
            if (url == null) {
                return null;
            }
            Invoker invoker = invokerCreator.createAsyncInvoker(ExceptionHandleService.class, url);
            invoke(invoker, proxy, method, methodName, paramClass, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

}
