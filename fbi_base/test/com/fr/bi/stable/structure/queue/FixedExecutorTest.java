package com.fr.bi.stable.structure.queue;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import junit.framework.TestCase;

/**
 * Created by daniel on 2017/1/10.
 */
public class FixedExecutorTest  extends TestCase {


    private volatile int basicLevel = 0;

    private class TestTask implements AVTask {

        private int level = -1;

        TestTask(int level) {
            this.level = level;
        }

        @Override
        public boolean isAvailable() {
            return basicLevel > level;
        }

        @Override
        public BICore getKey() {
            return BIBasicCore.generateValueCore(String.valueOf(level));
        }

        @Override
        public void work() {
            basicLevel++;
            System.out.println(level);
        }
    }


    public void testFixExecutor() throws InterruptedException {

        FixedExecutor executor = new FixedExecutor();
        for(int i = -1; i < 100; i ++) {
            executor.addTask(new TestTask(i));
        }
        Thread.sleep(1000);
        assertEquals(basicLevel, 101);
        basicLevel = 0;
        for(int i = 100; i > -1; i --) {
            executor.addTask(new TestTask(i - 1));
        }
        Thread.sleep(1000);
        assertEquals(basicLevel, 101);
    }
}
