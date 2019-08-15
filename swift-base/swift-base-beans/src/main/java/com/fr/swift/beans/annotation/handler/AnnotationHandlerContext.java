package com.fr.swift.beans.annotation.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description 便于后期的扩展注解
 */
public class AnnotationHandlerContext {
    private final static AnnotationHandlerContext INSTANCE = new AnnotationHandlerContext();

    //初始化绑定的生命周期
    private List<BeanHandler> startHandlers = new LinkedList<>();
    //完成初始化，开始执行方法阶段的生命周期
    private List<BeanHandler> methodHandlers = new LinkedList<>();
    //方法执行结束，替换对象的生命周期
    private List<BeanHandler> classHandlers = new LinkedList<>();
    //全部结束后，销毁的生命周期
    private List<BeanHandler> endHandlers = new LinkedList<>();

    private AnnotationHandlerContext() {
        // TODO: 19-8-14 动态绑定
        startHandlers.add(new SwiftAutowiredHandler());
        methodHandlers.add(new SwiftInitHandler());
        classHandlers.add(new SwiftAspectHandler());
        endHandlers.add(new SwiftDestroyHandler());
    }

    public static AnnotationHandlerContext getInstance() {
        return INSTANCE;
    }


    public void process(Object object, Class<?> clazz) {
        //按照生命周期的顺序执行
        startProcess(object, clazz);
        methodProcess(object, clazz);
        classProcess(object, clazz);
    }


    public void startProcess(Object object, Class<?> clazz) {
        for (BeanHandler beanHandler : startHandlers) {
            try {
                beanHandler.handle(object, clazz);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void endProcess(Object object, Class<?> clazz) {
        for (BeanHandler beanHandler : endHandlers) {
            try {
                beanHandler.handle(object, clazz);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void methodProcess(Object object, Class<?> clazz) {
        for (BeanHandler beanHandler : methodHandlers) {
            try {
                beanHandler.handle(object, clazz);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void classProcess(Object object, Class<?> clazz) {
        for (BeanHandler beanHandler : classHandlers) {
            try {
                beanHandler.handle(object, clazz);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
