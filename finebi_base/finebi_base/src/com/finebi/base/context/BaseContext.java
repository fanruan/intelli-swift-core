package com.finebi.base.context;

import com.fr.third.springframework.aop.config.AopConfigUtils;
import com.fr.third.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import com.fr.third.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.fr.third.springframework.context.support.GenericApplicationContext;

import java.lang.annotation.Annotation;

/**
 * Created by andrew_asa on 2017/12/17.
 * 基本容器，不用配置xml，默认有基于注解方式的aop特性
 * <p>
 * 新特性：
 * 1：基于class扫描构造bean定义(done)
 * 2：自动开启注解aop(done)
 * 3：动态aop注入（待定）
 */
public class BaseContext extends GenericApplicationContext {

    /**
     * 注解类扫描器
     */
    private final ClassPathBeanDefinitionScanner scanner;

    /**
     * 注解扫描器
     */
    private final AnnotatedBeanDefinitionReader reader;

    public BaseContext() {

        this(new DefaultListableBeanFactory(), null);
    }


    public BaseContext(DefaultListableBeanFactory beanFactory) {

        this(beanFactory, null);
    }


    public BaseContext(ApplicationContext parent) {

        this(new DefaultListableBeanFactory(), parent);
    }


    public BaseContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {

        super(beanFactory, parent);
        // class扫描器
        this.scanner = new ClassPathBeanDefinitionScanner(this);

        // class注解解读器
        this.reader = new AnnotatedBeanDefinitionReader(this);
        // 基于注解方式的aop开启
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(this);
        // 容器刷新 主要是装配需要的前置处理器等
        refresh();
    }

    //---------------------------------------------------------------------
    // 新特性添加的方法
    //---------------------------------------------------------------------
    public void addScanPath(String... paths) {

        scanner.scan(paths);
    }

    /**
     * 注册bean
     *
     * @param annotatedClasses
     */
    public void register(Class<?>... annotatedClasses) {

        for (Class<?> annotatedClass : annotatedClasses) {
            registerBean(annotatedClass);
        }
    }

    public void registerBean(Class<?> annotatedClass) {

        registerBean(annotatedClass, null, (Class<? extends Annotation>[]) null);
    }

    /**
     * @param annotatedClass
     * @param qualifiers     注解类，如果类上没有配置注解，则可以用参数来表示注解
     */
    public void registerBean(Class<?> annotatedClass,
                             @SuppressWarnings("unchecked") Class<? extends Annotation>... qualifiers) {

        registerBean(annotatedClass, null, qualifiers);
    }

    public void registerBean(Class<?> annotatedClass, String name,
                             @SuppressWarnings("unchecked") Class<? extends Annotation>... qualifiers) {

        reader.registerBean(annotatedClass, name, qualifiers);
    }

    public <T> T getObject(String beanName) {

        return (T) getBean(beanName);
    }

    public <T> T getObject(String beanName, Object... args) {

        return (T) getBean(beanName, args);
    }
}
