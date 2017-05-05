package com.fr.bi.stable.utils.program;
/**
 * This class created on 2017/4/26.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;

public class BIStringUtilsTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIStringUtilsTest.class);

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/26
     */
    public void testObjectJoin() {
        try {
            Object o = new Object() {
                @Override
                public String toString() {
                    return "ef";
                }
            };
            String result = BIStringUtils.append("ab", o, null);
            assertEquals(result, "abefnull");
            String result2 = BIStringUtils.append(null, "ab", o);
            assertEquals(result2, "nullabef");
            String result3 = BIStringUtils.append(o, null, "cd");
            assertEquals(result3, "efnullcd");
            String result4 = BIStringUtils.append("ab", null, o);
            assertEquals(result4, "abnullef");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }
}
