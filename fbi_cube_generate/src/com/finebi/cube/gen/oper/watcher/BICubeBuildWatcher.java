package com.finebi.cube.gen.oper.watcher;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.message.IMessageBody;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeBuildWatcher implements IProcessor {
    protected IPublish messagePublish;

    @Override
    public void process() {
        try {
            messagePublish.publicFinishMessage(generateFinishBody(""));
        } catch (BIDeliverFailureException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void setPublish(IPublish publish) {
        messagePublish = publish;
    }

    protected IMessageBody generateFinishBody(final String data) {
        return new IMessageBody() {
            @Override
            public String getMessageBody() {
                return data;
            }
        };
    }

    @Override
    public Object getResult() {
        return null;
    }
}
