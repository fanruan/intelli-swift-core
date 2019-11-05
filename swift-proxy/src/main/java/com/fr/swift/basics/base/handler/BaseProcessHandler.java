package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.Target;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class BaseProcessHandler<T> extends AbstractProcessHandler<T> implements ProcessHandler {

    public BaseProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
//        MonitorUtil.start();
//        Class proxyClass = method.getDeclaringClass();
//        Class<?>[] parameterTypes = method.getParameterTypes();
//        List<URL> urls = processUrl(target);
//        String methodName = method.getName();
        List<Object> result = new ArrayList<Object>();
//        if (null == urls || urls.isEmpty()) {
//            Invoker invoker = new LocalInvoker(ProxyServiceRegistry.INSTANCE.getService(proxyClass), proxyClass, null);
//            return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
//        }
//        for (URL url : urls) {
//            Invoker invoker = createInvoker(proxyClass, url);
//            result.add(invoke(invoker, proxyClass, method, methodName, parameterTypes, args));
//        }
//        MonitorUtil.finish(methodName);
        return mergeResult(result);
    }

    /**
     * 合并Result
     *
     * @param resultList
     * @param args
     * @return
     */
    protected abstract Object mergeResult(List resultList, Object... args) throws Throwable;

}
