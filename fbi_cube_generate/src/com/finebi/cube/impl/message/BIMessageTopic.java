package com.finebi.cube.impl.message;

import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageTopic implements IMessageTopic {

    private ITopicTag topicTag;

    public BIMessageTopic(ITopicTag topicTag) {
        this.topicTag = topicTag;
    }

    @Override
    public ITopicTag getTopicTag() {
        return topicTag;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIMessageTopic{");
        sb.append("topicTag=").append(topicTag);
        sb.append('}');
        return sb.toString();
    }
}
