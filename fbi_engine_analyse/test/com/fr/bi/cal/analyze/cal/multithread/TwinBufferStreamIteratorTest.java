package com.fr.bi.cal.analyze.cal.multithread;

import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2017/6/22.
 */
public class TwinBufferStreamIteratorTest extends TestCase {
    public void test(){
        final TwinBufferStreamIterator iterator = new TwinBufferStreamIterator(1000);
        final Thread[] ts = new Thread[100];
        for (int i = 0; i < 100;i++){
            ts[i] = new Thread(){
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++){
                        iterator.add(new Object());
                    }
                    iterator.wakeUp();
                }
            };
        }
        for (int i = 0; i < 100;i++){
            ts[i].start();
        }
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 100;i++){
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
        assertEquals(index, 10000);
    }
}
