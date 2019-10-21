package com.fr.swift;

import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.annotation.Target;
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
        assertEquals(Target.NONE.name(), "NONE");
        assertEquals(Target.HISTORY.name(), "HISTORY");
        assertEquals(Target.ANALYSE.name(), "ANALYSE");
        assertEquals(Target.REAL_TIME.name(), "REAL_TIME");
        assertEquals(Target.INDEXING.name(), "INDEXING");
        assertEquals(Target.ALL.name(), "ALL");

    }
}
