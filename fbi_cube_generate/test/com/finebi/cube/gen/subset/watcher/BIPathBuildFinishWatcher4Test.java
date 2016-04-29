package com.finebi.cube.gen.subset.watcher;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.oper.watcher.BIPathBuildFinishWatcher;
import com.finebi.cube.gen.subset.BICubeBuildProbeTool;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPathBuildFinishWatcher4Test extends BIPathBuildFinishWatcher {
    @Override
    public void process() {
        System.out.println("Path Build Finish");
        try {
            messagePublish.publicFinishMessage(generateFinishBody(""));
        } catch (BIDeliverFailureException e) {
            e.printStackTrace();
        }
        BICubeBuildProbeTool.INSTANCE.getFlag().put("Path Build Finish", 51);
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
