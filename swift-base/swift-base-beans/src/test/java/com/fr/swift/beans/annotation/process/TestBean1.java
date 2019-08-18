package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;
import com.fr.swift.beans.annotation.SwiftScope;

/**
 * @author anner
 * @this class created on date 2019/8/11
 * @description 没有继承类
 */
@SwiftBean(name = "testBean1")
@SwiftScope(value = SwiftScope.SINGLETON)
public class TestBean1 {

    @SwiftAutoWired
    private TestBean4 testBean4;

    @SwiftInitMethod
    public void testInitMethod(){

    }
    @SwiftDestroy
    public void testDestroyMethod(){

    }
}
