package com.finebi.cube.impl.operate;

import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIProcessor4Test implements IProcessor<Integer> {
    private boolean isReceiveMess;

    public BIProcessor4Test() {
        this.isReceiveMess = false;
    }

    @Override
    public void process() {
        isReceiveMess = true;
    }

    @Override
    public Integer getResult() {
        return null;
    }

    public boolean isReceiveMess() {
        return isReceiveMess;
    }

    @Override
    public void setPublish(IPublish publish) {

    }
}
