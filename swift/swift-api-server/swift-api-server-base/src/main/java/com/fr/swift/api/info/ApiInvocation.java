package com.fr.swift.api.info;

public class ApiInvocation {

    private String methodName;

    private Class<?> aClass;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public ApiInvocation(String methodName, Class<?> aClass, Class<?>[] parameterTypes, Object[] arguments) {
        this.methodName = methodName;
        this.aClass = aClass;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
