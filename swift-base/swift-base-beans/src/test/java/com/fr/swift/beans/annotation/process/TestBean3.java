package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;
import com.fr.swift.beans.annotation.SwiftQualilifer;

/**
 * @author anner
 * @this class created on date 2019/8/12
 * @description
 */
@SwiftBean(name = "testBean3")
public class TestBean3 {
    @SwiftAutoWired
    @SwiftQualilifer
    private TestBean1 testBean1;
    @SwiftAutoWired
    private TestBean2 testBean2;
    @SwiftDestroy
    public void testInitMethod(){

    }
}
