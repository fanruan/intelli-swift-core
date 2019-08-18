package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;

/**
 * @author anner
 * @this class created on date 2019/8/18
 * @description
 */
@SwiftBean(name = "testBean1")
public class TestBean1 {

    public static int number = 0;

    @SwiftInitMethod
    public void init() {
        number++;
    }

    @SwiftDestroy
    private void destroy() {
        number -= 10;
    }

    public void test() {

    }

    //测试aspect的目标方法
    public void run() {
        System.out.println("target method in testBean1 is running");
    }
}
