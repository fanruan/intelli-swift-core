package com.finebi.cube.impl.pubsub;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicTagThreshold extends BIBasicThreshold<ITopicTag> {
    @Override
    public ITopicTag getTargetTag(IMessage message) {
        return message.getTopic().getTopicTag();
    }

    @Override
    protected boolean handleOrNot(IMessage message) {
        return message.isTopicMessage();
    }
}
