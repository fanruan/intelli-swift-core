package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.InvokeMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public class BaseInvocationHandler implements InvokerHandler {

    private InvokerCreater invokerCreater;

    public BaseInvocationHandler(InvokerCreater invokerCreater) {
        this.invokerCreater = invokerCreater;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvokeMethod invokeMethod = method.getAnnotation(InvokeMethod.class);
        if (null != invokeMethod) {
            // TODO: 2018/11/1 类第一次构造，之后加缓存
            Class<? extends ProcessHandler> handlerClass = invokeMethod.value();
            Constructor<? extends ProcessHandler> cons = handlerClass.getDeclaredConstructor(InvokerCreater.class);
            ProcessHandler handler = cons.newInstance(invokerCreater);
            return handler.processResult(method, invokeMethod.target(), args);
        }
        throw new UnsupportedOperationException("Method must wrapped by " + InvokeMethod.class);
    }

}
