package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBefore;
import com.fr.swift.beans.annotation.SwiftPointCut;
import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/20
 * @description
 */
public class SwiftAspectProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        Class<?> clazz = beanDefinition.getClazz();
        if (clazz.isAnnotationPresent(SwiftAspect.class)) {
            beanDefinition.setAspect(true);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(SwiftPointCut.class)) {
                    beanDefinition.setPointCut(method);
                    String[] targetList = method.getAnnotation(SwiftPointCut.class).targets();
                    beanDefinition.setAdviceTarget(targetList);
                }
                if (method.isAnnotationPresent(SwiftBefore.class)) {
                    beanDefinition.setBeforeMethod(method);
                }
                if (method.isAnnotationPresent(SwiftAfter.class)) {
                    beanDefinition.setAfterMethod(method);
                }
            }
        }
    }
}
