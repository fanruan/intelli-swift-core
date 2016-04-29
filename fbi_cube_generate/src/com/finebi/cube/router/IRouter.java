package com.finebi.cube.router;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * 消息的路由中转
 * <p/>
 * PUB/SUB消息发布订阅模式。
 * 当有消息传递过来时候，首先是发送到路由中。
 * 路由首先是依据消息的主题，找到相应的Topic。如果当前的消息是
 * 针对当前Topic的消息，那么将消息发送给所有该Topic的订阅者。
 * 如果不是，那么发送给消息中的分片。Fragment收到该消息，依然判断
 * 当前消息是属于分片消息，还是分片中的状态消息。
 * 如果是分片消息，那么就发送给分片的订阅者，如果是状态消息那么发送到
 * 相应状态的订阅者。
 * <p/>
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IRouter {
    /**
     * 传递消息
     *
     * @param message 消息
     */
    void deliverMessage(IMessage message) throws BIDeliverFailureException;

    /**
     * 在路由中注册一个主题。
     * 必须要先注册一个主题，然后才能进行订阅相应主题，或者发布相应主题的消息。
     *
     * @param topic 注册一个主题
     */
    void registerTopic(ITopic topic) throws BITopicDuplicateException;

    void registerTopic(ITopicTag topicTag) throws BITopicDuplicateException;

    /**
     * 在某一主题下注册一个分片。
     *
     * @param fragmentTag
     * @param topicTag    主题
     * @param fragment    主题的分片
     */
    void registerFragment(ITopicTag topicTag, IFragment fragment) throws BITopicAbsentException, BIFragmentDuplicateException;

    void registerFragment(ITopicTag topicTag, IFragmentTag fragment) throws BITopicAbsentException, BIFragmentDuplicateException;

    void registerStatus(ITopicTag topicTag, IFragmentTag fragmentTag, IStatus status) throws
            BITopicAbsentException, BIFragmentAbsentException, BIStatusDuplicateException;

    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param subscribe   订阅者
     * @param topicTag    主题
     * @param fragmentTag
     */
    IFragment subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BISubscribeDuplicateException;


    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param status      状态
     * @param subscribe   订阅者
     * @param topicTag    主题
     * @param fragmentTag 主题分片
     * @param statusTag
     */

    IStatus subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BIStatusAbsentException, BISubscribeDuplicateException;


    /**
     * 订阅主题下的全部消息
     *
     * @param subscribe 订阅者
     * @param topicTag  主题
     * @return
     * @throws BITopicAbsentException
     * @throws BISubscribeDuplicateException
     */
    ITopic subscribe(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException, BISubscribeDuplicateException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag)
            throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag)
            throws BITopicAbsentException, BIFragmentAbsentException;

    boolean isRegistered(ITopicTag topicTag);

    boolean isRegistered(IFragmentTag fragmentTag);

    boolean isRegistered(IStatusTag statusTag);

    void reset();
}
