package com.finebi.base.context;

import com.finebi.springframework.aop.config.AopConfigUtils;
import com.finebi.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.finebi.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import com.finebi.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.finebi.springframework.core.type.filter.AnnotationTypeFilter;
import com.finebi.springframework.stereotype.Controller;
import com.finebi.springframework.web.bind.annotation.ControllerAdvice;
import com.finebi.springframework.web.context.support.GenericWebApplicationContext;

import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;

/**
 * Created by andrew_asa on 2017/12/19.
 */
public class BaseWebAppContext extends GenericWebApplicationContext {
    /**
     * 注解类扫描器
     */
    private final ClassPathBeanDefinitionScanner scanner;

    /**
     * 注解扫描器
     */
    private final AnnotatedBeanDefinitionReader reader;

    public BaseWebAppContext() {

        this(new DefaultListableBeanFactory(), null);
    }


    public BaseWebAppContext(DefaultListableBeanFactory beanFactory) {

        this(beanFactory, null);
    }


    public BaseWebAppContext(ServletContext servletContext) {

        this(new DefaultListableBeanFactory(), servletContext);
    }


    public BaseWebAppContext(DefaultListableBeanFactory beanFactory, ServletContext servletContext) {

        super(beanFactory, servletContext);
        // class扫描器
        this.scanner = new ClassPathBeanDefinitionScanner(this);
        // class注解解读器
        this.reader = new AnnotatedBeanDefinitionReader(this);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ControllerAdvice.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        // 基于注解方式的aop开启
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(this);
        // 容器刷新 主要是装配需要的前置处理器等
        //refresh();
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
}
