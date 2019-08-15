package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftBefore;
import com.fr.swift.beans.annotation.SwiftPointCut;
import com.fr.swift.beans.annotation.aop.SwiftJoinPoint;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
@SwiftBean(name = "beanWithAspect")
@SwiftAspect
public class BeanWithAspect {

    @SwiftPointCut(targets = {"com.fr.swift.beans.annotation.handle.BeanWithAutowired.run",
                              "com.fr.swift.beans.annotation.handle.BeanWithMethod.run"})
    private void test(){
    }
    @SwiftBefore
    public void before(SwiftJoinPoint joinPoint){
        System.out.println(joinPoint);
        System.out.println("before");

    }
    @SwiftAfter
    public void after(SwiftJoinPoint joinPoint){
        System.out.println(joinPoint);
        System.out.println("after");
    }
}
