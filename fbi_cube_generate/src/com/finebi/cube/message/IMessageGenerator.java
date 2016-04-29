package com.finebi.cube.message;

import com.finebi.cube.exception.BIMessageGenerateException;

/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessageGenerator {
    IMessage generateMessage(IMessageBody body) throws BIMessageGenerateException;

    void changeMessageTopicTag(IMessageTopic topicTag);

    void changeMessageFragmentTag(IMessageFragment fragmentTag);

    void changeMessageStatusTag(IMessageStatus statusTag);

    IMessageTopic getMessageTopicTag();

    IMessageFragment getMessageFragmentTag();

    IMessageStatus getMessageStatusTag();
}
