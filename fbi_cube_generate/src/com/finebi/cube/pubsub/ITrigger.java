package com.finebi.cube.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface ITrigger {
    ITriggerThreshold getThreshold();

    void setTriggerCount(int count);

    boolean keepTriggerOn();

    /**
     * 添加一个主题，与此函数添加的主题是与关系
     *
     * @param topicTag 主题
     */
    void addAndTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个分片，与此函数添加的分片是与关系
     *
     * @param fragmentTag 主题
     */
    void addAndFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个状态，与此函数添加的全部状态是与关系
     *
     * @param statusTag 主题
     */
    void addAndStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个主题，添加的全部主题是与关系
     *
     * @param topicTag 主题
     */

    void addOrTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个分片，添加的全部分片是与关系
     *
     * @param fragmentTag 主题
     */
    void addOrFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个分片，添加的全部分片是与关系
     *
     * @param statusTag 主题
     */
    void addOrStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 处理消息
     *
     * @param message
     */
    void handleMessage(IMessage message) throws BIThresholdIsOffException;

    String leftCondition();
}
