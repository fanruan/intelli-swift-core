package com.finebi.cube.message;

import com.finebi.cube.router.topic.ITopicTag;

/**
 * Message的主题。无论是消息发布者还是监听者都首先要约定好主题。
 * 从而监听者才能正确的获得目标对象发布的消息。
 * 除此以外Fragment也是确定对象非常重要的。
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessageTopic {
    ITopicTag getTopicTag();
}
