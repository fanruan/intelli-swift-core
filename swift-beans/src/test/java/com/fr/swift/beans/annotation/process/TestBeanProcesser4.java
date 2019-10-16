package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftBefore;
import com.fr.swift.beans.annotation.SwiftPointCut;
import com.fr.swift.beans.annotation.aop.SwiftJoinPoint;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
@SwiftBean
@SwiftAspect
public class TestBeanProcesser4 {
    @SwiftPointCut(targets = {"com.fr.swift.beans.annotation.process.TestBeanProcesser2.run",
            "com.fr.swift.beans.annotation.process.TestBeanProcesser3.run"})
    private void test() {
    }

    @SwiftBefore
    public void before(SwiftJoinPoint joinPoint) {
        System.out.println(joinPoint);
        System.out.println("before");

    }

    @SwiftAfter
    public void after(SwiftJoinPoint joinPoint) {
        System.out.println(joinPoint);
        System.out.println("after");
    }
}
