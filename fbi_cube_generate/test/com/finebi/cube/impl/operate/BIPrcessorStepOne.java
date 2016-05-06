package com.finebi.cube.impl.operate;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/3/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPrcessorStepOne implements IProcessor {
    private IPublish publish;

    @Override
    public void process(IMessage lastReceiveMessage) {
        try {
            publish.publicRunningMessage(null);
            System.out.println("Start First Step,");
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            System.out.println("Stop First Step,");
            publish.publicStopMessage(null);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void setPublish(IPublish publish) {
        this.publish = publish;
    }
}
