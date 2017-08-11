package com.fr.bi.cal.analyze.cal.multithread;

import com.fr.bi.cal.analyze.session.BISession;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小灰灰 on 2016/8/8.
 */
public class BIMultiThreadExecutor {
    private static final int SIZE = Math.max(1 << 2, Math.min(1 << 4,Runtime.getRuntime().availableProcessors()));
    private static final int MOD = SIZE - 1;
    private MergeSummaryCallList[] lists = new MergeSummaryCallList[SIZE];
    private int index = 0;
    public static AtomicInteger count =new AtomicInteger(0);

    public BIMultiThreadExecutor() {
        for (int i = 0; i < SIZE; i++) {
            MergeSummaryCallList list = new MergeSummaryCallList("BIMultiThreadExecutor" + i);
            list.start();
            lists[i] = list;
        }
    }

    public void add(BISingleThreadCal cal) {
        lists[index & MOD].add(cal);
        index++;
    }

    /**
     * 唤醒下下面wait的线程，可能队列里面没达到自动唤醒的阈值
     */
    public void wakeUp(){
        for (MergeSummaryCallList list : lists){
            list.wakeUp();
        }
    }

    protected void releaseCurrentThread(final BISession session) {
        for (MergeSummaryCallList list : lists) {
            list.add(new BISingleThreadCal() {
                @Override
                public void cal() {
                    session.getLoader().releaseCurrentThread();
                }
            });
            list.wakeUp();
        }
    }

}
