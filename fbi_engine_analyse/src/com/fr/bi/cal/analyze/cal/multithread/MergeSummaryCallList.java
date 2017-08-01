package com.fr.bi.cal.analyze.cal.multithread;

/**
 * Created by 小灰灰 on 2016/8/5.
 */
public class MergeSummaryCallList extends Thread {
    private TwinBufferStreamIterator<BISingleThreadCal> iterator;

    public MergeSummaryCallList() {
        iterator = new TwinBufferStreamIterator<BISingleThreadCal>(1<<10);
    }

    public MergeSummaryCallList(String name) {
        super(name);
        iterator = new TwinBufferStreamIterator<BISingleThreadCal>(1<<10);
    }

    public void add(BISingleThreadCal cal){
        iterator.add(cal);
    }

    @Override
    public void run() {
        while (true){
            try {
                while (iterator.hasNext()){
                    iterator.next().cal();
                }
            } catch (Exception e){
            }
        }
    }

    protected void wakeUp() {
        iterator.wakeUp();
    }
}
