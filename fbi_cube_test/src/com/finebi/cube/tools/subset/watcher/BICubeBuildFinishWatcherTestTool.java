package com.finebi.cube.tools.subset.watcher;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.oper.watcher.BICubeBuildFinishWatcher;
import com.finebi.cube.tools.BICubeBuildProbeTool;
import com.finebi.cube.message.IMessage;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildFinishWatcherTestTool extends BICubeBuildFinishWatcher {
    @Override
    public void process(IMessage lastReceiveMessage) {
        System.out.println("FineIndex Build Finish");
        try {
            messagePublish.publicFinishMessage(generateFinishBody(""));
        } catch (BIDeliverFailureException e) {
            e.printStackTrace();
        }

        BICubeBuildProbeTool.INSTANCE.getFlag().put("FineIndex Build Finish", 51);
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
