package com.fr.swift.context;

import com.fr.swift.util.Crasher;
import com.fr.third.springframework.beans.factory.support.AbstractBeanDefinition;
import com.fr.third.springframework.beans.factory.support.BeanDefinitionBuilder;
import com.fr.third.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.fr.third.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.context.ConfigurableApplicationContext;
import com.fr.third.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This class created on 2018-1-30 16:58:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftContext extends AnnotationConfigApplicationContext {
    private static final SwiftContext INSTANCE = new SwiftContext();
    private static ConfigurableApplicationContext configurableContext;
    private static BeanDefinitionRegistry beanDefinitionRegistry;
    private boolean refreshed = false;

    private SwiftContext() {
    }

    public static void init() {
        if (INSTANCE.refreshed) {
            return;
        }
        synchronized (INSTANCE) {
            if (INSTANCE.refreshed) {
                return;
            }

            try {
                INSTANCE.register(Class.forName("com.fr.swift.boot.SwiftContextConfiguration"));
                INSTANCE.refresh();
            } catch (ClassNotFoundException e) {
                Crasher.crash(e);
                return;
            }

            INSTANCE.refreshed = true;
            configurableContext = INSTANCE;
            beanDefinitionRegistry = (DefaultListableBeanFactory) configurableContext.getBeanFactory();
        }
    }

    /**
     * 手动注册bean
     *
     * @param beanId
     * @param className
     */
    public static void registerBean(String beanId, String className) {
        registerBean(beanId, className, null, null);
    }

    /**
     * 手动注册bean 指定InitMethod和destroyMethod
     *
     * @param beanId
     * @param className
     * @param initMethodName
     * @param destroyMethodName
     */
    public static void registerBean(String beanId, String className, String initMethodName, String destroyMethodName) {
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(className);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setInitMethodName(initMethodName);
        beanDefinition.setDestroyMethodName(destroyMethodName);
        beanDefinitionRegistry.registerBeanDefinition(beanId, beanDefinition);
    }

    /**
     * 手动注销bean
     *
     * @param beanId
     */
    public static void unregisterBean(String beanId) {
        beanDefinitionRegistry.removeBeanDefinition(beanId);
    }

    public static ApplicationContext get() {
        return INSTANCE;
    }
}