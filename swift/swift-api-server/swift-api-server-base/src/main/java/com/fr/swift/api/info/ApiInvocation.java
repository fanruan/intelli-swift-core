package com.fr.swift.api.info;

public class ApiInvocation {

    private String methodName;

    private Class<?> target;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public ApiInvocation(String methodName, Class<?> target, Class<?>[] parameterTypes, Object[] arguments) {
        this.methodName = methodName;
        this.target = target;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getTarget() {
        return target;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
