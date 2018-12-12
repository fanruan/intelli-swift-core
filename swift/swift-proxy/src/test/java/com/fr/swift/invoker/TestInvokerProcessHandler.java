package com.fr.swift.invoker;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;

import java.lang.reflect.Method;

/**
 * This class created on 2018/11/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TestInvokerProcessHandler extends AbstractProcessHandler {

    public TestInvokerProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Object processUrl(Target target, Object... args) {
        return null;
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        System.out.println("remote invoke");
        return null;
    }
}
