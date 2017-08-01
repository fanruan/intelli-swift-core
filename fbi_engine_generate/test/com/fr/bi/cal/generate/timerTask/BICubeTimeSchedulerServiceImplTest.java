package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.conf.base.scheduler.BITimeScheduleService;
import com.fr.bi.conf.base.scheduler.BITimeSchedulerManager;
import com.fr.bi.conf.base.scheduler.BITimeSchedulerManagerTestTool;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.Date;


/**
 * Created by neil on 2017/7/31.
 */
public class BICubeTimeSchedulerServiceImplTest extends TestCase {


    public void testAddTimeScheduler() {
        BICubeTimeSchedulerServiceImplTesTool timeSchedulerService = new BICubeTimeSchedulerServiceImplTesTool();
        int preExecutSize = timeSchedulerService.getBITimeSchedulerManager().getAllScheduleEntity().size();
        timeSchedulerService.addTimeScheduler("1 * * * * ?", null, -999L, 0, new Date(System.currentTimeMillis()), null);
        assertTrue((timeSchedulerService.getBITimeSchedulerManager().getAllScheduleEntity().size() - preExecutSize) == 1);
    }

    public void testRemoveAllTimeSchedulers() {
        BICubeTimeSchedulerServiceImplTesTool timeSchedulerService = new BICubeTimeSchedulerServiceImplTesTool();
        int preExecutSize = timeSchedulerService.getBITimeSchedulerManager().getAllScheduleEntity().size();
        timeSchedulerService.addTimeScheduler("1 * * * * ?", null, -999L, 0, new Date(System.currentTimeMillis()), null);
        assertTrue((timeSchedulerService.getBITimeSchedulerManager().getAllScheduleEntity().size()) > preExecutSize);
        timeSchedulerService.removeAllTimeSchedulers();
        assertTrue((timeSchedulerService.getBITimeSchedulerManager().getAllScheduleEntity().size()) == preExecutSize);
    }

}