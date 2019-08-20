package com.fr.swift.beans.annotation.handle;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

/**
 * @author anner
 * @this class created on date 2019/8/18
 * @description
 */
@SwiftBean
public class TestBeanHandler2 {
    @SwiftAutoWired
    public TestBeanHandler1 testBean1;

    //测试aspect的目标方法
    public void run() {
        System.out.println("target method in testBean2 is running");
    }
}
