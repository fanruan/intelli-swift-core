package com.finebi.cube.gen.oper.observer;

import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.message.IMessage;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
class FinishObserverProcessor extends BIProcessor<String> {
    private boolean success = true;

    public boolean success() {
        return success;
    }

    @Override
    public String mainTask(IMessage lastReceiveMessage) {
        if (lastReceiveMessage.isStopStatus()) {
            success = false;
            return "***********************warning:the cube build failure*****************************";

        }
        return "catch the Message about finish status of cube Building";
    }

    @Override
    protected void initThreadPool() {
        executorService = BIProcessorThreadManager.getInstance().getExecutorService();
    }

    @Override
    protected boolean disposeStopMessage() {
        return true;
    }

    @Override
    public void release() {

    }
}
