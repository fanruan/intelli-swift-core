package com.fr.bi.cal.analyze.cal.multithread;

/**
 * Created by 小灰灰 on 2016/8/8.
 */
public class BIMultiThreadExecutor {
    private static final int SIZE = 1 << 3;
    private static final int MOD = SIZE - 1;
    private MergeSummaryCallList[] lists = new MergeSummaryCallList[SIZE];
    private int index = 0;
    public BIMultiThreadExecutor() {
        for (int i = 0; i < SIZE; i ++){
            MergeSummaryCallList list = new MergeSummaryCallList();
            MultiThreadManagerImpl.getInstance().getExecutorService().submit(list);
            lists[i] = list;
        }
    }

    public void add(BISingleThreadCal cal){
        lists[index & MOD].add(cal);
        index++;
    }

    public void awaitExecutor(){
        for (MergeSummaryCallList list : lists){
            list.finish();
        }
        lists = null;
        MultiThreadManagerImpl.getInstance().awaitExecutor();
    }
}
