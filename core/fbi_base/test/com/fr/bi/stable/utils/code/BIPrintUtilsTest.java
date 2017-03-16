package com.fr.bi.stable.utils.code;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import junit.framework.TestCase;

/**
 * This class created on 2016/8/19.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BIPrintUtilsTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/8/19
     */
    public void testPrintException() {
        try {
            try {
                throw new NullPointerException("");
            } catch (NullPointerException e) {
                throw BINonValueUtils.beyondControl(e);
            }

        } catch (Exception e) {
            System.out.println(BIPrintUtils.outputException(e));
        }
    }
}
