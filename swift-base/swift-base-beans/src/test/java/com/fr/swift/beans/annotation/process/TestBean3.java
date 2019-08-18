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
@SwiftBean(name = "testBean3")
public class TestBean3 extends TestBean2 {
    @SwiftAutoWired
    @SwiftQualifier(name = "testBean2")
    private TestBean2 testBean2;

    @SwiftAutoWired
    @SwiftQualifier(name = "testBean1")
    private TestBean1 testBean1Test;

    @SwiftAutoWired
    @SwiftQualifier(name = "testBean1")
    private TestBean1 testBean11;

    @SwiftDestroy
    public void testInitMethod() {

    }
}
