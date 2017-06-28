package com.fr.bi.cal.analyze.cal.multithread;

import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2017/6/22.
 */
public class TwinBufferStreamIteratorTest extends TestCase {
    private static final int BUFFER_SIZE = 1000;
    private static final int THREAD_SIZE = 100;
    private static final int TASK_COUNT = 100;
    public void test(){
        final TwinBufferStreamIterator iterator = new TwinBufferStreamIterator(BUFFER_SIZE);
        final Thread[] ts = new Thread[THREAD_SIZE];
        for (int i = 0; i < THREAD_SIZE;i++){
            ts[i] = new Thread(){
                @Override
                public void run() {
                    for (int j = 0; j < TASK_COUNT; j++){
                        iterator.add(new Object());
                    }
                    iterator.wakeUp();
                }
            };
        }
        for (int i = 0; i < THREAD_SIZE;i++){
            ts[i].start();
        }
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < THREAD_SIZE;i++){
                    try {
                        ts[i].join();
                    } catch (InterruptedException e) {
                    }
                }
                iterator.finish();
            }
        }.start();
        int index = 0;
        while (iterator.hasNext()){
            iterator.next();
            index++;
        }
        assertEquals(index, TASK_COUNT * THREAD_SIZE);
    }
}
