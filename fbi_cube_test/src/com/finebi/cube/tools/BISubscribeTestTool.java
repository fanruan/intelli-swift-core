package com.finebi.cube.tools;

import com.finebi.cube.tools.operate.BIPrcessorStepOne;
import com.finebi.cube.impl.pubsub.BISubscribe;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribeID;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribeTestTool extends BISubscribe {
    public boolean receiveMessage = false;

    public BISubscribeTestTool(ISubscribeID subscribeID) {
        super(subscribeID, new BIPrcessorStepOne());
    }

    @Override
    public void handleMessage(IMessage message) {
        System.out.println(message.getTopic().toString());
        receiveMessage = true;
    }

    public void resetAlarm() {
        receiveMessage = false;
    }


}
