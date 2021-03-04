package com.fr.swift.cloud.local;

import com.fr.swift.cloud.basics.InvokerType;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvokerCreator extends AbstractInvokerCreator {

    @Override
    public InvokerType getType() {
        return InvokerType.LOCAL;
    }
}
