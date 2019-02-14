package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerHandler;

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
