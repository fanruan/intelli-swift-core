package com.finebi.cube.pubsub;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.message.IMessageBody;
import com.finebi.cube.message.IMessageFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * 发布消息的接口
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IPublish {

    void publicMessage(IMessageBody body) throws BIDeliverFailureException;

    void setTopicTag(ITopicTag topicTag);

    void setFragmentTag(IFragmentTag fragmentTag);

    void setStatusTag(IStatusTag tag);

    void publicFinishMessage(IMessageBody body) throws BIDeliverFailureException;

    void publicStopMessage(IMessageBody body) throws BIDeliverFailureException;

    void publicWaitingMessage(IMessageBody body) throws BIDeliverFailureException;

    void publicRunningMessage(IMessageBody body) throws BIDeliverFailureException;

    IMessageFragment getFragmentTag();
}
