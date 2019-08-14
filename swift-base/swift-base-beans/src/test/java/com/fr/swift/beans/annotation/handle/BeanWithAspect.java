package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftAfter;
import com.fr.swift.beans.annotation.SwiftAspect;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftBefore;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
@SwiftBean(name = "beanWithAspect")
@SwiftAspect
public class BeanWithAspect {
    public  int getNumber() {
        return number;
    }

    public static int number=0;
    @SwiftBefore
    public void before(){
        System.out.println("before");
        ++number;
    }
    public int testNormal(){
        System.out.println("pointCut");
        return number;
    }
    @SwiftAfter
    public void after(){
        System.out.printf("after");
        ++number;
    }
}
