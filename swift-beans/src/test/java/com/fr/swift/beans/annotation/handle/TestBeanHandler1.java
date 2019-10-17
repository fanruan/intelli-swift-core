package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;

/**
 * @author anner
 * @this class created on date 2019/8/18
 * @description
 */
@SwiftBean
public class TestBeanHandler1 {

    private int number = 0;

    @SwiftInitMethod
    public void init() {
        number += 1;
    }

    @SwiftDestroy
    private void destroy() {
        number = -1;
    }

    public int getNumber() {
        return number;
    }

    public void test() {

    }

    //测试aspect的目标方法
    public void run() {
        System.out.println("target method in testBean1 is running");
    }
}
