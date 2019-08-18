package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftBefore;
import com.fr.swift.beans.annotation.SwiftPointCut;
import com.fr.swift.beans.annotation.aop.SwiftJoinPoint;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
import com.fr.swift.log.SwiftLoggers;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/13
 * @description 完成容器对象的代理
 */
public class SwiftAspectHandler implements BeanHandler {

    @Override
    public void handle(final Object object, final Class<?> clazz) throws ClassNotFoundException {
        //通知的目标方法,每一个aspect默认只存在一个before和after，多个无法确定执行顺序
        Method beforeMethod = null, afterMethod = null;
        //切点的目标位置
        String[] targets = {};
        //扫描class，初始化基本的信息
        if (clazz.isAnnotationPresent(SwiftAspect.class)) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                //找到通知的方法
                if (method.isAnnotationPresent(SwiftBefore.class)) {
                    beforeMethod = method;
                }
                if (method.isAnnotationPresent(SwiftAfter.class)) {
                    afterMethod = method;
                }
                //找到切点
                if (method.isAnnotationPresent(SwiftPointCut.class)) {
                    SwiftPointCut pointCut = method.getAnnotation(SwiftPointCut.class);
                    //获取到需要代理的方法位置
                    targets = pointCut.targets();
                }
            }
            for (String target : targets) {
                //需要代理的类
                final String className = target.substring(0, target.lastIndexOf("."));
                final String methodName = target.substring(target.lastIndexOf(".") + 1, target.length());
                //获取代理类的实例
                String beanName = getBeanNametByClassName(className);
                final Object targetObject = SwiftBeanRegistry.getInstance().getSingletonObjects().get(beanName);
                if (targetObject == null) {
                    throw new SwiftBeanException(" aspect " + clazz.getName() + ": pointcut " + target + " object is null");
                }
                //开始创建代理对象
                final Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(targetObject.getClass());
                final Method finalBeforeMethod = beforeMethod;
                final Method finalAfterMethod = afterMethod;
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        Object returnValue = null;
                        //初始化joinPoint
                        SwiftJoinPoint joinPoint = new SwiftJoinPoint(targetObject, o, method, objects, className, methodName);
                        //判断方法是否是目标方法
                        if (method.getName().equals(methodName)) {
                            //开始执行before
                            try {
                                finalBeforeMethod.invoke(object, joinPoint);
                            } catch (Exception e) {
                                SwiftLoggers.getLogger().error(String.format("aspect %s -->  %s before method error", clazz.getName(), className));
                            }
                            //执行目标方法
                            returnValue = method.invoke(targetObject, objects);
                            //开始执行after
                            try {
                                finalAfterMethod.invoke(object, joinPoint);
                            } catch (Exception e) {
                                SwiftLoggers.getLogger().error(String.format("aspect %s --> %s after method error", clazz.getName(), className));
                            }
                        } else {
                            returnValue = method.invoke(targetObject, objects);
                        }
                        return returnValue;
                    }
                });
                //替换容器中的对象
                SwiftBeanRegistry.getInstance().getSingletonObjects().put(beanName, enhancer.create());
            }
        }
    }

    //根据类名获取BeanName
    private String getBeanNametByClassName(String className) throws ClassNotFoundException {
        String beanName;
        //根据类的全路径名获取class对象
        Class<?> clazz;
        //启动容器的线程获取classLoader
        clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        //获取到注解,判断是否被托管
        if (clazz.isAnnotationPresent(SwiftBean.class)) {
            beanName = clazz.getAnnotation(SwiftBean.class).name();
        } else {
            throw new SwiftBeanException(String.format("target %s is not marked as swiftBean", clazz.getName()));
        }
        return beanName;
    }

}
