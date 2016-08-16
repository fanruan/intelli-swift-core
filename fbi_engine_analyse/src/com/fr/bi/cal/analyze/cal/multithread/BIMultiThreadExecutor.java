package com.fr.bi.cal.analyze.cal.multithread;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/8/8.
 */
public class BIMultiThreadExecutor {
    public static void execute(List<BISingleThreadCal> list){
        int size = (list.size() >> 3) + 1;
        for (int i = 0; i < 8; i++) {
            int start = i * size;
            if (start > list.size()) {
                break;
            }
            int end = Math.min(start + size, list.size());
            MultiThreadManagerImpl.getInstance().getExecutorService().submit(new MergeSummaryCallList(list.subList(start, end)));
        }
        MultiThreadManagerImpl.getInstance().awaitExecutor();
    }
}
