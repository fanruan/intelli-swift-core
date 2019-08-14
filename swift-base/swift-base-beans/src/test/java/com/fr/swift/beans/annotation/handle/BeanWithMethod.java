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
}
