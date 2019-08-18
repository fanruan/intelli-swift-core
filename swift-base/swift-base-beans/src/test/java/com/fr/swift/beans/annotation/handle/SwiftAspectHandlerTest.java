package com.fr.swift.beans.annotation.handle;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.handler.AnnotationHandlerContext;
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

    public void testProcess() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        AnnotationHandlerContext annotationHandlerContext=AnnotationHandlerContext.getInstance();
        //没有替换代理对象之前
        assertSame(TestBean1.class, SwiftContext.get().getBean(TestBean1.class).getClass());
        assertSame(TestBean2.class, SwiftContext.get().getBean(TestBean2.class).getClass());

        annotationHandlerContext.classProcess();

        //替换代理之后
        assertNotSame(TestBean1.class, SwiftContext.get().getBean(TestBean1.class).getClass());
        assertNotSame(TestBean2.class, SwiftContext.get().getBean(TestBean2.class).getClass());
        //输出打印结果
        SwiftContext.get().getBean(TestBean1.class).run();
        SwiftContext.get().getBean(TestBean2.class).run();
    }
}
