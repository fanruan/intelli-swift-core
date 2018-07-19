package com.fr.swift.netty.rpc.annotation;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.context.support.ClassPathXmlApplicationContext;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AnnotationTest extends TestCase {

    public void testAnnotation() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(RpcService.class);
        assertNotSame(serviceBeanMap.size(), 0);
        Object object = serviceBeanMap.get("testCalculatorService");
        boolean hasMethod = false;
        for (Method method : object.getClass().getMethods()) {
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                assertTrue(rpcMethod.methodName().equals("add") || rpcMethod.methodName().equals("multiply"));
                hasMethod = true;
            }
        }
        assertTrue(hasMethod);
    }
}
