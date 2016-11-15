package com.fr.bi.cal.analyze.cal.multithread;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.session.BISession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 小灰灰 on 2016/8/8.
 */
public class BIMultiThreadExecutor {
    private static final int SIZE = 1 << 3;
    private static final int MOD = SIZE - 1;
    private MergeSummaryCallList[] lists = new MergeSummaryCallList[SIZE];
    private int index = 0;
    private ExecutorService executorService;
    public BIMultiThreadExecutor() {
        executorService = Executors.newFixedThreadPool(SIZE);
        for (int i = 0; i < SIZE; i ++){
            MergeSummaryCallList list = new MergeSummaryCallList();
            executorService.submit(list);
            lists[i] = list;
        }
    }

    public void add(BISingleThreadCal cal){
        lists[index & MOD].add(cal);
        index++;
    }

    public void awaitExecutor(final BISession session){
        for (MergeSummaryCallList list : lists){
            list.add(new BISingleThreadCal() {
                @Override
                public void cal() {
                    session.getLoader().releaseCurrentThread();
                }
            });
            list.finish();
        }
        lists = null;
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }
}
