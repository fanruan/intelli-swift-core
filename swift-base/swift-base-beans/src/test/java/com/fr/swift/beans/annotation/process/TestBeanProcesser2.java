package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;
import com.fr.swift.beans.annotation.SwiftQualifier;
import com.fr.swift.beans.annotation.SwiftScope;

/**
 * @author anner
 * @this class created on date 2019/8/11
 * @description 只有一个父类 test Bean1
 */
@SwiftBean
@SwiftScope(value = SwiftScope.PROTOTYPE)
public class TestBeanProcesser2 extends TestBeanProcesser1 {

    @SwiftAutoWired
    @SwiftQualifier(name = "testBeanProcesser1")
    public TestBeanProcesser1 testBean1;

    @SwiftInitMethod
    public void testInitMethod() {

    }

    @SwiftDestroy
    public void testDestroyMethod() {

    }
}
