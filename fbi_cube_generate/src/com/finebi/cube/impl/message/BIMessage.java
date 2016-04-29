package com.finebi.cube.impl.message;

import com.finebi.cube.message.*;

/**
 *
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessage implements IMessage {
    final private IMessageTopic topicTag;
    final private IMessageFragment fragmentTag;
    final private IMessageStatus statusTag;
    final private IMessageBody body;

    public BIMessage(IMessageTopic topicTag, IMessageFragment fragmentTag, IMessageStatus statusTag, IMessageBody body) {
        this.topicTag = topicTag;
        this.fragmentTag = fragmentTag;
        this.statusTag = statusTag;
        this.body = body;
    }

    @Override
    public IMessageTopic getTopic() {
        return topicTag;
    }

    @Override
    public IMessageFragment getFragment() {
        return fragmentTag;
    }

    @Override
    public IMessageStatus getStatus() {
        return statusTag;
    }

    @Override
    public IMessageBody getBody() {
        return body;
    }

    @Override
    public boolean isTopicMessage() {
        return topicTag != null && fragmentTag == null && statusTag == null;
    }

    @Override
    public boolean isFragmentMessage() {
        return fragmentTag != null && statusTag == null;
    }

    @Override
    public boolean isStatusMessage() {
        return statusTag != null;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIMessage{");
        sb.append("topicTag=").append(topicTag);
        sb.append(", fragmentTag=").append(fragmentTag);
        sb.append(", statusTag=").append(statusTag);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }
}
