package com.fr.bi.cal.analyze.cal.multithread;

import com.fr.bi.cal.analyze.executor.detail.StreamPagedIterator;

import java.util.concurrent.Callable;

/**
 * Created by 小灰灰 on 2016/8/5.
 */
public class MergeSummaryCallList implements Callable {
    private StreamPagedIterator<BISingleThreadCal> iterator;

    public MergeSummaryCallList() {
        iterator = new StreamPagedIterator<BISingleThreadCal>();
        iterator.setMaxCount(1 << 10);
    }

    public void add(BISingleThreadCal cal){
        iterator.addCell(cal);
    }

    public void finish(){
        iterator.finish();
    }

    @Override
    public Object call() throws Exception {
        while (iterator.hasNext()){
            iterator.next().cal();
        }
        return null;
    }
}
