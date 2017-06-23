package com.fr.bi.cal.analyze.cal.multithread;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2016/8/5.
 */
public class MergeSummaryCallList implements Callable {
    private TwinBufferStreamIterator<BISingleThreadCal> iterator;
    public static final AtomicInteger count = new AtomicInteger(0);

    public MergeSummaryCallList() {
        iterator = new TwinBufferStreamIterator<BISingleThreadCal>(1<<10);
    }

    public void add(BISingleThreadCal cal){
        iterator.add(cal);
    }

    public void finish(){
        iterator.finish();
    }

    @Override
    public Object call() throws Exception {
        while (iterator.hasNext()){
            iterator.next().cal();
            count.getAndIncrement();
        }
        return null;
    }

    protected void wakeUp() {
        iterator.wakeUp();
    }
}
