package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
@SwiftBean(name = "beanWithAutowired")
public class BeanWithAutowired {
    @SwiftAutoWired
    private BeanWithAspect beanWithAspect;

    @SwiftAutoWired
    private BeanWithMethod beanWithMethod;

    public BeanWithAspect getBeanWithAspect() {
        return beanWithAspect;
    }

    public BeanWithMethod getBeanWithMethod() {
        return beanWithMethod;
    }

    //测试aspect的目标方法
    public void run(){
        System.out.println("target method in beanWithAutowired is running");
    }
}
