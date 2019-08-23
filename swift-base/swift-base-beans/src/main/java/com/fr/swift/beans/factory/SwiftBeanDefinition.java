package com.fr.swift.beans.factory;

import com.fr.swift.beans.annotation.SwiftScope;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private boolean aspect;

    private Map<Field, String> autowiredFields = new HashMap<>();

    private Method initMethod = null;

    private Method destroyMethod = null;

    public Method getPointCut() {
        return pointCut;
    }

    public void setPointCut(Method pointCut) {
        this.pointCut = pointCut;
    }

    public String[] getAdviceTarget() {
        return adviceTarget;
    }

    public void setAdviceTarget(String[] adviceTarget) {
        this.adviceTarget = adviceTarget;
    }

    public Method getBeforeMethod() {
        return beforeMethod;
    }

    public void setBeforeMethod(Method beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public Method getAfterMethod() {
        return afterMethod;
    }

    public void setAfterMethod(Method afterMethod) {
        this.afterMethod = afterMethod;
    }

    private Method pointCut = null;

    private String[] adviceTarget = {};

    private Method beforeMethod = null;

    private Method afterMethod = null;

    public SwiftBeanDefinition(Class<?> clazz, String beanName) {
        this(clazz, beanName, SwiftScope.SINGLETON);
    }

    public SwiftBeanDefinition(Class<?> clazz, String beanName, String scope) {
        this.clazz = clazz;
        this.beanName = beanName;
        this.scope = scope;
    }

    public boolean isAspect() {
        return aspect;
    }

    public void setAspect(boolean aspect) {
        this.aspect = aspect;
    }


    public Method getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Map<Field, String> getAutowiredFields() {
        return autowiredFields;
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

    public List<Field> getAllAutowiredFiles() {
        return new ArrayList<>(autowiredFields.keySet());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += 31 * beanName.hashCode() + 7;
        hash += 31 * clazz.getName().hashCode() + 7;
        hash += 31 * scope.hashCode() + 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SwiftBeanDefinition definition = (SwiftBeanDefinition) obj;
        return this.getClazz().getName().equals(definition.getClazz().getName()) && this.getBeanName().equals(definition.getBeanName());
    }

}
