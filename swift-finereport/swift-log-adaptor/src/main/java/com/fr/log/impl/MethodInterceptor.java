package com.fr.log.impl;

import com.fr.third.net.bytebuddy.implementation.bind.annotation.Origin;
import com.fr.third.net.bytebuddy.implementation.bind.annotation.RuntimeType;
import com.fr.third.net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable)
            throws Exception {
        return callable.call();
    }
}
