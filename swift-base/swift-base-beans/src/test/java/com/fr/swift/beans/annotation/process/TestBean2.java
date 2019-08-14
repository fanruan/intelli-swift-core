package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;
import com.fr.swift.beans.annotation.SwiftScope;

/**
 * @author anner
 * @this class created on date 2019/8/11
 * @description
 */
@SwiftBean(name = "testBean2")
@SwiftScope(value = SwiftScope.PROTOTYPE)
public class TestBean2 {

    @SwiftAutoWired
    private TestBean1 testBean1;

    @SwiftInitMethod
    public void testInitMethod() {

    }

    @SwiftDestroy
    public void testDestroyMethod() {

    }
}
