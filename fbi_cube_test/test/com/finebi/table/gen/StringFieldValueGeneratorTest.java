package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class StringFieldValueGeneratorTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(StringFieldValueGeneratorTest.class);

    /**
     * Detail:
     * Author:Connery
     * Date:2017/5/17
     */
    public void testStringSpeed() {
        try {
            long time = System.currentTimeMillis();
            StringFieldValueGenerator generator = new StringFieldValueGenerator(100000, 100000000);
            time = System.currentTimeMillis() - time;
            /**
             * 机器不同，情况不同。在i5-4200u+ddr3下的结果是60ms-70ms结果。
             */
            assertTrue(time < 100);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/5/17
     */
    public void testStringRead() {
        try {
            Set<String> valueSet = new HashSet<String>();
            int group = 100;
            int row = 1000;
            StringFieldValueGenerator generator = new StringFieldValueGenerator(group, row);
            for (int i = 0; i < row; i++) {
                valueSet.add(generator.getValue());
            }
            assertTrue(valueSet.size() > group - 10);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }


}
