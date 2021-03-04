package com.fr.swift.cloud;

import com.fr.swift.cloud.basics.InvokerType;
import com.fr.swift.cloud.basics.annotation.Target;
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

    public void testTarget() {
        assertEquals(Target.HISTORY.name(), "HISTORY");
        assertEquals(Target.ANALYSE.name(), "ANALYSE");
        assertEquals(Target.REAL_TIME.name(), "REAL_TIME");
        assertEquals(Target.ALL.name(), "ALL");
    }
}
