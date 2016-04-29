package com.fr.bi.utility;

import junit.framework.TestCase;

/**
 * Created by Connery on 2014/12/14.
 */
public class TestTimeAssert extends TestCase {
    private long startPoint;

    public void beginRecord() {
        startPoint = System.currentTimeMillis();
    }

    public void addAssertTime(long millis, long range) {
        long span = System.currentTimeMillis() - startPoint;
        System.out.println("span:" + span);
        System.out.println("upper:" + (millis + range));
        System.out.println("lower" + (millis - range));
        assertTrue((millis + range > span) && (millis - range < span));
    }
}