package com.fr.swift.local;

import com.fr.swift.basics.InvokerType;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvokerCreater extends AbstractInvokerCreater {

    @Override
    public InvokerType getType() {
        return InvokerType.LOCAL;
    }
}
