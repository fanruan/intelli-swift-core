package com.fr.bi.cal.generate.timerTask;

import com.fr.bi.conf.base.scheduler.BITimeSchedulerManager;
import com.fr.bi.conf.base.scheduler.BITimeSchedulerManagerTestTool;

/**
 * Created by neil on 2017/7/31.
 */
public class BICubeTimeSchedulerServiceImplTesTool extends BICubeTimeSchedulerServiceImpl {

    public BICubeTimeSchedulerServiceImplTesTool() {
        timeSchedulerManager = new BITimeSchedulerManagerTestTool();
    }

    public BITimeSchedulerManager getBITimeSchedulerManager() {
        return timeSchedulerManager;
    }
}
