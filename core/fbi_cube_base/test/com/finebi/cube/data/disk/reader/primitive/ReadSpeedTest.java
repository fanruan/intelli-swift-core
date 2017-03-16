package com.finebi.cube.data.disk.reader.primitive;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import junit.framework.TestCase;

/**
 * This class created on 2016/6/22.
 *
 * @author Connery
 * @since 4.0
 */
public class ReadSpeedTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/22
     */
    public void testPrimitiveBoxSpeed() {
        try {
            Long value = BIRandomUitils.getRandomLong();
            long time = System.currentTimeMillis();
            Long min = Long.MIN_VALUE;
            Long sum = Long.valueOf(0);
            for (int i = 0; i < 10000000; i++) {
                value++;
                if (!value.equals(min)) {
                    sum+=value;
                }
            }
            System.out.println(sum);
            System.out.println(System.currentTimeMillis() - time);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testPrimitiveBoxSpeed_2() {
        try {
            Long value = BIRandomUitils.getRandomLong();
            long time = System.currentTimeMillis();
            Long min = Long.MIN_VALUE;
            long sum = 0;
            for (int i = 0; i < 10000000; i++) {
                value++;
                if (value!=(min)) {
                    sum+=value;
                }
            }
            System.out.println(sum);
            System.out.println(System.currentTimeMillis() - time);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
