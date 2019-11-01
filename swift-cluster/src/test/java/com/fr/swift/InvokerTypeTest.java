package com.fr.swift;

import com.fr.swift.basics.InvokerType;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class InvokerTypeTest extends TestCase {

    public void testInvokerType() {
        assertEquals(InvokerType.REMOTE.name(), "REMOTE");
        assertEquals(InvokerType.LOCAL.name(), "LOCAL");
    }

}
