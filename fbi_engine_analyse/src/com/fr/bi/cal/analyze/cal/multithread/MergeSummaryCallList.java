package com.fr.bi.cal.analyze.cal.multithread;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2016/8/5.
 */
public class MergeSummaryCallList implements Callable {
    private List<MergeSummaryCall> callList;

    public MergeSummaryCallList(List<MergeSummaryCall> callList) {
        this.callList = callList;
    }

    @Override
    public Object call() throws Exception {
        if (callList != null){
            for (MergeSummaryCall call : callList){
                call.cal();
            }
        }
        return null;
    }
}
