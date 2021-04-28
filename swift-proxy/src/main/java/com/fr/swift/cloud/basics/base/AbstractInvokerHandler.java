package com.fr.swift.cloud.basics.base;

import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.InvokerHandler;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractInvokerHandler implements InvokerHandler {

    protected InvokerCreator invokerCreator;

    public AbstractInvokerHandler(InvokerCreator invokerCreator) {
        this.invokerCreator = invokerCreator;
    }
}
