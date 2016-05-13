package com.finebi.cube;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/4/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIThreadEnvTest extends TestCase {
    public void testThread() {
        int count = 4;
        final int amount = 50;
        final List<Integer> disk = new ArrayList<Integer>();
        final List<Integer> seed = new ArrayList<Integer>();
        try {

            for (int i = 0; i < count; i++) {
                Thread.sleep(10);
                seed.add(BIRandomUitils.getRandomInteger());
            }
            for (int i = 0; i < count; i++) {

                new Thread(new innerRun(i, disk, amount)).start();

            }
            while (disk.size() != (amount * count)) {
                Thread.sleep(10);
            }
            checkResult(disk);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkResult(List<Integer> disk) {
        if (disk.size() < 2) {
            assertFalse(true);
        }
        int sum = 0;
        for (int i = 1; i < disk.size(); i++) {
            int temp = disk.get(i) - disk.get(i - 1) != 0 ? 1 : 0;
//            System.out.println(disk.get(i));
            sum += temp;
        }
        System.out.println(sum);
        assertTrue(sum != 3);
    }

    private class innerRun implements Runnable {
        int count;
        List<Integer> disk;
        int amount;

        public innerRun(int count, List<Integer> disk, int amount) {
            this.count = count;
            this.disk = disk;
            this.amount = amount;
        }

        @Override
        public void run() {
            for (int j = 0; j < amount; j++) {
                try {
                    Thread.sleep(Math.abs(BIRandomUitils.getRandomInteger()) % 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (disk) {
                    disk.add(count);
                }

            }
        }
    }
}
