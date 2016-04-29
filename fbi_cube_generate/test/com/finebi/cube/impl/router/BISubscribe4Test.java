package com.finebi.cube.impl.router;

import com.finebi.cube.impl.operate.BIPrcessorStepOne;
import com.finebi.cube.impl.pubsub.BISubscribe;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribeID;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribe4Test extends BISubscribe {
    protected boolean receiveMessage = false;

    public BISubscribe4Test(ISubscribeID subscribeID) {
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
