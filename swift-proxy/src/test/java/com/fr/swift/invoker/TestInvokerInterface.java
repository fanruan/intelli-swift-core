package com.fr.swift.invoker;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface TestInvokerInterface {

    @InvokeMethod(value = ProcessHandler.class, target = Target.NONE)
    String print(String id, String name, int age, long time);
}
