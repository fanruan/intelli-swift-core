package com.fr.swift.beans.annotation.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author anner
 * @this class created on date 2019/8/15
 * @description 增强target
 */
public class SwiftJoinPoint {
    //目標對象
    private Object target;
    //代理對象
    private Object proxy;
    //代理方法
    Method proxyMethod;
    //代理方法的參數
    Object[] args;
    //目標className
    private String className;
    //目標方法名
    private String methodName;

    public SwiftJoinPoint(Object target, Object proxy, Method proxyMethod, Object[] args, String className, String methodName) {
        this.target = target;
        this.proxy = proxy;
        this.proxyMethod = proxyMethod;
        this.args = args;
        this.className = className;
        this.methodName = methodName;
    }

    public Object getTarget() {
        return target;
    }

    public Object getProxy() {
        return proxy;
    }

    public Method getProxyMethod() {
        return proxyMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "SwiftJoinPoint{" +
                "target=" + target +
                ", proxy=" + proxy +
                ", proxyMethod=" + proxyMethod +
                ", args=" + Arrays.toString(args) +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
