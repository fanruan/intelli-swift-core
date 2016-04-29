package com.finebi.cube.impl.operate;

import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/3/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPrcessorStepTwo implements IProcessor {
    private IPublish publish;

    @Override
    public void process() {
        try {
            publish.publicRunningMessage(null);
            System.out.println("Start Second Step");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            System.out.println("Stop  Second Step");
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
