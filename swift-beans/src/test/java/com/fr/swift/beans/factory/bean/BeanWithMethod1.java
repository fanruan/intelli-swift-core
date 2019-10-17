package com.fr.swift.beans.factory.bean;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.annotation.SwiftInitMethod;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
@SwiftBean(name = "beanWithMethod1")
public class BeanWithMethod1 {
    public static int number=0;
    private static int test=10;
    @SwiftInitMethod
    public void init(){
        if(test==0){
            number+=10;
        }
    }
    @SwiftDestroy
    public void destroy(){
        test-=10;
    }
}
