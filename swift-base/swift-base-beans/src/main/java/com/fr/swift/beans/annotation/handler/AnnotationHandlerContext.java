package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private Map<Object, SwiftBeanDefinition> getObjectMap() {
        Map<Object, SwiftBeanDefinition> objectMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : SwiftBeanRegistry.getInstance().getSingletonObjects().entrySet()) {
            objectMap.put(entry.getValue(), SwiftBeanRegistry.getInstance().getBeanDefinition(entry.getKey()));
        }
        return objectMap;
    }

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


    public void process() {
        //先执行代理
        classProcess();
        startProcess();
        methodProcess();

    }

    //每一个流程保证全部的对象全部执行完毕，否则后面的流程会出现逻辑问题，比如autowired必须全部set完毕
    public void startProcess() {
        for (BeanHandler beanHandler : startHandlers) {
            for (Map.Entry<Object, SwiftBeanDefinition> objectClassEntry : getObjectMap().entrySet()) {
                beanHandler.handle(objectClassEntry.getKey(), objectClassEntry.getValue());
            }
        }
    }

    public void endProcess() {
        for (BeanHandler beanHandler : endHandlers) {
            for (Map.Entry<Object, SwiftBeanDefinition> objectClassEntry : getObjectMap().entrySet()) {
                beanHandler.handle(objectClassEntry.getKey(), objectClassEntry.getValue());
            }
        }
    }

    public void methodProcess() {
        for (BeanHandler beanHandler : methodHandlers) {
            for (Map.Entry<Object, SwiftBeanDefinition> objectClassEntry : getObjectMap().entrySet()) {
                beanHandler.handle(objectClassEntry.getKey(), objectClassEntry.getValue());
            }
        }
    }

    public void classProcess() {
        for (BeanHandler beanHandler : classHandlers) {
            for (Map.Entry<Object, SwiftBeanDefinition> objectClassEntry : getObjectMap().entrySet()) {
                beanHandler.handle(objectClassEntry.getKey(), objectClassEntry.getValue());
            }
        }
    }
}
