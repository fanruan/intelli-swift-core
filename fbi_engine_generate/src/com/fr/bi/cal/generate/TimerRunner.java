package com.fr.bi.cal.generate;


import com.finebi.cube.conf.singletable.BICubeTimeTaskCreator;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorManager;
import com.fr.stable.bridge.StableFactory;

/**
 * Created by GUY on 2015/3/31.
 * edit by kary on 2015/6/21
 */
public class TimerRunner {

    protected BIUser biUser;
    protected BICubeTimeTaskCreator biCubeTimeTaskCreator;

    public TimerRunner(long userId) {
        biUser = new BIUser(userId);
        init();
    }

    public void init() {
        biCubeTimeTaskCreator= StableFactory.getMarkedObject(BICubeTimeTaskCreator.XML_TAG,BICubeTimeTaskCreatorManager.class);
        biCubeTimeTaskCreator.taskCreate(biUser.getUserId());
    }

    public void envChanged() {
        init();
    }
}
