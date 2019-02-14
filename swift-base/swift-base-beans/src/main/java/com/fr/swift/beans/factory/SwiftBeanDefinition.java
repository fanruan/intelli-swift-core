package com.fr.swift.beans.factory;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanDefinition {

    public static final String SINGLETON = "singleton";

    public static final String PROTOTYPE = "prototype";

    private Class<?> clazz;

    private String beanName;

    private String scope;

//    private String initMethod;
//
//    private String destroyMethod;

    public SwiftBeanDefinition(Class<?> clazz, String beanName) {
        this(clazz, beanName, SINGLETON);
    }

    public SwiftBeanDefinition(Class<?> clazz, String beanName, String scope) {
        this.clazz = clazz;
        this.beanName = beanName;
        this.scope = scope;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public boolean singleton() {
        return this.scope.equals(SINGLETON);
    }
}
