package com.finebi.cube.gen.sub;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.impl.pubsub.BISubscribe;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.finebi.cube.pubsub.ISubscribeID;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeFinishWatcher extends BISubscribe {
    private IPublish publish;

    public BICubeFinishWatcher(ISubscribeID subscribeID, final IPublish publish) {
        super(subscribeID, new IProcessor() {
            @Override
            public void process() {
                try {
                    publish.publicFinishMessage(null);
                } catch (BIDeliverFailureException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }

            @Override
            public void setPublish(IPublish publish) {

            }

            @Override
            public Object getResult() {
                return null;
            }
        });
    }

}
