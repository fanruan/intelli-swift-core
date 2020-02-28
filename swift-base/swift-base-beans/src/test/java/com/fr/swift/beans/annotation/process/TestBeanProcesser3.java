package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftQualifier;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description 存在两个父类
 */
@SwiftBean
public class TestBeanProcesser3 extends TestBeanProcesser2 {
    @SwiftAutoWired
    @SwiftQualifier(name = "testBeanProcesser2")
    private TestBeanProcesser2 testBean2;

    @SwiftAutoWired
    @SwiftQualifier(name = "testBeanProcesser1")
    private TestBeanProcesser1 testBean1Test;

    @SwiftAutoWired
    @SwiftQualifier(name = "testBeanProcesser1")
    private TestBeanProcesser1 testBean11;

    @SwiftDestroy
    public void testInitMethod() {

    }
}
