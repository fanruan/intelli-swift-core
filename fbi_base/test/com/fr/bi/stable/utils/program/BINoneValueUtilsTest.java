package com.fr.bi.stable.utils.program;

import junit.framework.TestCase;

/**
 * This class created on 2016/7/28.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BINoneValueUtilsTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/28
     */
    public void testExceptionCause() {
        try {
            try {
                try {
                    try {
                        throw new NullPointerException("ABC");
                    } catch (NullPointerException e) {
                        throw BINonValueUtils.beyondControl(e);
                    }
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            } catch (Exception e) {
                assertEquals(e.getCause().getMessage(), "ABC");
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}