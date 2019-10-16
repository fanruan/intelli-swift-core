package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anner
 * @this class created on date 2019/8/8
 * @description
 */
public class AnnotationProcesserContext {
    private final static AnnotationProcesserContext INSTANCE = new AnnotationProcesserContext();

    private List<BeanProcesser> processers = new ArrayList<>();

    private AnnotationProcesserContext() {
        // TODO: 2019/8/9  processers 动态绑定
        processers.add(new SwiftScopeProcesser());
        processers.add(new SwiftAutowiredProcesser());
        processers.add(new SwiftInitProcesser());
        processers.add(new SwiftDestroyProcesser());
        processers.add(new SwiftAspectProcesser());
    }

    public static AnnotationProcesserContext getInstance() {
        return INSTANCE;
    }

    public void process(SwiftBeanDefinition beanDefinition) {
        for (BeanProcesser processer : processers) {
            processer.process(beanDefinition);
        }
    }

}
