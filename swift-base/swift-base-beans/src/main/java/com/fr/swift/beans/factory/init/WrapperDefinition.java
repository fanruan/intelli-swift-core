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
        int hash = definition.getClazz().getName().hashCode() * 31 + count;
        hash += definition.getBeanName().hashCode() * 31 + count;
        hash += definition.getInitMethod().hashCode() * 31 + count;
        hash += definition.getDestroyMethod().hashCode() * 31 + count ;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WrapperDefinition) {
            WrapperDefinition wrapperDefinition = (WrapperDefinition) obj;
            return definition.getClazz().getName().equals(wrapperDefinition.definition.getClazz().getName()) && definition.getBeanName().equals(wrapperDefinition.definition.getBeanName()) && count == wrapperDefinition.count;
        }
        return false;
    }
}
