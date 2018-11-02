package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProcessHandler extends AbstractProcessHandler implements ProcessHandler {

    public BaseProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<URL> urls = processUrl(target);
        String methodName = method.getName();
        List result = new ArrayList();
        if (null == urls || urls.isEmpty()) {
            Invoker invoker = new LocalInvoker(ProxyServiceRegistry.INSTANCE.getService(proxyClass), proxyClass, null);
            return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        }
        for (URL url : urls) {
            Invoker invoker = createInvoker(proxyClass, url);
            result.add(invoke(invoker, proxyClass, method, methodName, parameterTypes, args));
        }
        MonitorUtil.finish(methodName);
        return mergeResult(result);
    }

    /**
     * 合并Result
     *
     * @param resultList
     * @return
     */
    protected abstract Object mergeResult(List resultList) throws Throwable;

    /**
     * 创建Invoker
     *
     * @param tClass
     * @param url
     * @return
     */
    protected abstract Invoker createInvoker(Class tClass, URL url);

}
