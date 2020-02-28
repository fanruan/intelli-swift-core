package com.fr.swift.beans.factory.init;

/*
 * This class created on 2019/8/12
 *
 * @author Krysta
 * @description
 * */

import com.fr.swift.beans.factory.SwiftBeanDefinition;

public class WrapperDefinition {
    SwiftBeanDefinition definition;
    private int count;

    public WrapperDefinition(SwiftBeanDefinition definition, int count) {
        this.definition = definition;
        this.count = count;
    }

    public SwiftBeanDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(SwiftBeanDefinition definition) {
        this.definition = definition;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        return definition.hashCode() + count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        WrapperDefinition wrapperDefinition = (WrapperDefinition) obj;
        return definition.getClazz().getName().equals(wrapperDefinition.definition.getClazz().getName()) && definition.getBeanName().equals(wrapperDefinition.definition.getBeanName()) && count == wrapperDefinition.count;
    }
}
