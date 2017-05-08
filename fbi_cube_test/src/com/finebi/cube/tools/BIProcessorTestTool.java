package com.finebi.cube.tools;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIProcessorTestTool implements IProcessor<Integer> {
    private boolean isReceiveMess;

    public BIProcessorTestTool() {
        this.isReceiveMess = false;
    }

    @Override
    public void process(IMessage lastReceiveMessage) {
        isReceiveMess = true;
    }

    @Override
    public Integer getResult() {
        return null;
    }

    @Override
    public void handleMessage(IMessage receiveMessage) {

    }

    public boolean isReceiveMess() {
        return isReceiveMess;
    }

    @Override
    public void setPublish(IPublish publish) {

    }
}
