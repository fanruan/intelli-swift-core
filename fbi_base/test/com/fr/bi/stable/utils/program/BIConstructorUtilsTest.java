package com.fr.bi.stable.utils.program;

import com.fr.bi.common.persistent.writer.BIIgnore4Test;
import junit.framework.TestCase;

/**
 * This class created on 2016/6/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BIConstructorUtilsTest extends TestCase {
    public void testUnsafeConstructor() {
        try {
            BIIgnore4Test test = BIConstructorUtils.unsafeConstructObject(BIIgnore4Test.class);
            assertTrue(test.getA() == null);
            assertTrue(test.getB() == null);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
