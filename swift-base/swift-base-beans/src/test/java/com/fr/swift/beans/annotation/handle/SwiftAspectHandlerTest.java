package com.fr.swift.beans.annotation.handle;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftPointCut;
import com.fr.swift.beans.annotation.handler.AnnotationHandlerContext;
import com.fr.swift.beans.annotation.handler.SwiftAspectHandler;
import com.fr.swift.beans.factory.BeanFactory;
import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class SwiftAspectHandlerTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        BeanFactory beanFactory= SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.annotation.handle");
        beanFactory.init();
        super.setUp();
    }
    public void testProcess() throws InvocationTargetException, IllegalAccessException {
        AnnotationHandlerContext annotationHandlerContext=AnnotationHandlerContext.getInstance();
        BeanWithAspect beanWithAspect=SwiftContext.get().getBean(BeanWithAspect.class);
        annotationHandlerContext.classProcess(beanWithAspect,beanWithAspect.getClass());
        SwiftContext.get().getBean(BeanWithAutowired.class).run();
        SwiftContext.get().getBean(BeanWithMethod.class).run();

        //测试joinPoint
        annotationHandlerContext.classProcess(beanWithAspect,beanWithAspect.getClass());
        SwiftContext.get().getBean(BeanWithAutowired.class).run();
        SwiftContext.get().getBean(BeanWithMethod.class).run();
    }
}
