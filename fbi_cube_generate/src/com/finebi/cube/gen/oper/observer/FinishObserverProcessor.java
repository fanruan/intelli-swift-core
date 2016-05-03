package com.finebi.cube.gen.oper.observer;

import com.finebi.cube.impl.pubsub.BIProcessor;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
class FinishObserverProcessor extends BIProcessor<String> {
    @Override
    public String mainTask() {
        return "catch the Message about finish status of cube Building";
    }

    @Override
    public void release() {

    }
}
