package com.finebi.cube.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * 订阅者的接口
 * 订阅者通过subscribe方法。
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface ISubscribe {

    void handleMessage(IMessage message);

    boolean keepSubscribe();

    ISubscribeID getSubscribeID();

    void closeVerbose();

    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param topicTag    主题
     * @param fragmentTag
     */
    void subscribe(IFragmentTag fragmentTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BIRegisterIsForbiddenException;

    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param status      状态
     * @param topicTag    主题
     * @param fragmentTag 主题分片
     * @param statusTag
     */

    void subscribe(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException,
            BIStatusAbsentException, BIRegisterIsForbiddenException;


    /**
     * 订阅主题下的全部消息
     *
     * @param topicTag 主题
     * @return
     * @throws BITopicAbsentException
     * @throws BISubscribeDuplicateException
     */
    void subscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException;

    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param topicTag    主题
     * @param fragmentTag
     */
    void orSubscribe(IFragmentTag fragmentTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BIRegisterIsForbiddenException;

    /**
     * 订阅相应主题下的分片
     *
     * @param fragment    主题分片
     * @param status      状态
     * @param topicTag    主题
     * @param fragmentTag 主题分片
     * @param statusTag
     */

    void orSubscribe(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException,
            BIStatusAbsentException, BIRegisterIsForbiddenException;


    /**
     * 订阅主题下的全部消息
     *
     * @param topicTag 主题
     * @return
     * @throws BITopicAbsentException
     * @throws BISubscribeDuplicateException
     */
    void orSubscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException;

    boolean isSubscribed(ITopicTag topicTag) throws BITopicAbsentException;

    boolean isSubscribed(IStatusTag statusTag)
            throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException;

    boolean isSubscribed(IFragmentTag fragmentTag)
            throws BITopicAbsentException, BIFragmentAbsentException;

    /**
     * 订阅消息监听的次数
     *
     * @param round
     */
    void subscribeRound(int round);
}
