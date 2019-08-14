package com.fr.swift.beans.factory;

import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.util.Strings;

import java.util.LinkedList;
import java.util.List;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanDefinition {

    private Class<?> clazz;

    private String beanName;

    private String scope;

    private boolean autoWired;

    private List<Class<?>> autowiredClassList = new LinkedList<>();

    private String initMethod = Strings.EMPTY;

    private String destroyMethod = Strings.EMPTY;

    public SwiftBeanDefinition(Class<?> clazz, String beanName) {
        this(clazz, beanName, SwiftScope.SINGLETON);
    }

    public SwiftBeanDefinition(Class<?> clazz, String beanName, String scope) {
        this.clazz = clazz;
        this.beanName = beanName;
        this.scope = scope;
    }


    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public List<Class<?>> getAutowiredClassList() {
        return autowiredClassList;
    }

    public void setAutowiredClassList(List<Class<?>> autowiredClassList) {
        this.autowiredClassList = autowiredClassList;
    }


    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean getAutoWired() {
        return autoWired;
    }

    public void setAutoWired(boolean autoWired) {
        this.autoWired = autoWired;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public boolean singleton() {
        return this.scope.equals(SwiftScope.SINGLETON);
    }
}
