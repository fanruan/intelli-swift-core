package com.fr.bi.utility;

/**
 * Created by Connery on 2014/12/14.
 */
public class TestUtil {
    /**
     * 占用CUP时间
     *
     * @param timeMillis 占用时长
     */
    public static void wasteTime(long timeMillis) {
        long timeRecord = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeRecord < timeMillis) {
            continue;
        }
    }

}