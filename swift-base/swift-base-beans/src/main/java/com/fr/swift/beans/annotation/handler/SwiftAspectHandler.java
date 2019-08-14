package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftBefore;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/13
 * @description 反射出一个无参的方法执行
 */
public class SwiftAspectHandler implements BeanHandler {

    @Override
    public void handle(Object object, Class<?> clazz) {
        String beanName = clazz.getAnnotation(SwiftBean.class).name();
        SwiftAspect swiftAspect = clazz.getAnnotation(SwiftAspect.class);
        Method beforeMethod = null, afterMethod = null;
        if (swiftAspect != null) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                SwiftBefore before = method.getAnnotation(SwiftBefore.class);
                SwiftAfter after = method.getAnnotation(SwiftAfter.class);
                if (before != null) beforeMethod = method;
                else if (after != null) afterMethod = method;
            }
        }
        //构造代理对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        Method finalBeforeMethod = beforeMethod;
        Method finalAfterMethod = afterMethod;
        Object finalObject = object;
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (finalBeforeMethod != null) {
                    finalBeforeMethod.invoke(finalObject);
                }
                Object obj = method.invoke(finalObject, objects);
                if (finalAfterMethod != null) {
                    finalAfterMethod.invoke(finalObject);
                }
                return obj;
            }
        });
        //替换原有的对象
        SwiftBeanRegistry.getInstance().getSingletonObjects().put(beanName, enhancer.create());
    }
}
