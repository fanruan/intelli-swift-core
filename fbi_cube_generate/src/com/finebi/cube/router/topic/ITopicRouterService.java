package com.finebi.cube.router.topic;

import com.finebi.cube.exception.*;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.IMessageDeliver;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface ITopicRouterService extends IMessageDeliver {


    /**
     * 在路由中注册一个主题。
     * 必须要先注册一个主题，然后才能进行订阅相应主题，或者发布相应主题的消息。
     *
     * @param topic 注册一个主题
     */
    void registerTopic(ITopic topic) throws BITopicDuplicateException;

    /**
     * 在某一主题下注册一个分片。
     *
     * @param fragmentTag
     * @param topicTag    主题
     * @param fragment    主题的分片
     */
    void registerFragment(ITopicTag topicTag, IFragment fragment) throws BITopicAbsentException, BIFragmentDuplicateException;

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
    IFragment subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag) throws
            BITopicAbsentException
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

    IStatus subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag
            statusTag) throws BITopicAbsentException
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
    ITopic subscribe(ISubscribe subscribe, ITopicTag topicTag) throws
            BITopicAbsentException, BISubscribeDuplicateException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag)
            throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException;

    boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag)
            throws BITopicAbsentException, BIFragmentAbsentException;

    boolean isRegistered(ITopicTag topicTag);

    boolean isRegistered(IFragmentTag fragmentTag);

    boolean isRegistered(IStatusTag statusTag);


    void reset();

    void closeVerbose();
}
