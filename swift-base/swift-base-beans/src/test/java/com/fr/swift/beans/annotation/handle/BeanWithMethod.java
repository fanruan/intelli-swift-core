package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
@SwiftBean(name = "beanWithMethod")
public class BeanWithMethod {
    public static int number=0;
    public static int test=10;
    @SwiftInitMethod
    public void testInitMethod(){
        number++;
    }
    @SwiftDestroy
    public void testDestroyMethod(){
        test++;
    }

    //测试aspect的目标方法
    public void run(){
        System.out.println("target method in beanWithMethod is running");
    }
}
