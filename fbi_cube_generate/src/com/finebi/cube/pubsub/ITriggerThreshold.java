package com.finebi.cube.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.impl.pubsub.BITriggerThreshold;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/23.
 * T指Topic，F-Fragment，S-Status
 * 一个Item包含T,F,S
 * T，F，S之间都与的关系。必须要T，F，S的否则都满足才能触发。
 * <p/>
 * Item之间Or关系。
 * 整个模型是：
 * （n个T的and）and（n个F的and）and（n个S的and）组合成Item
 * n个Item的Or组成最后的Threshold
 * <p/>
 * <p/>
 * 操作中
 * 默认第一个Item是And的集合，所有不指定Index的addAnd操作都默认加到
 * 第一个Item中。
 * 每进行一个Or操作，都会顺序产生一个新的Item，如果要在其中添加其它条件，那么指定index
 * <p/>
 * 阀值注册，T1注册到T集合中，但是F1可能superT是T1，F1注册到F集合。
 * 如果收到F1的消息，那么，此时应该只能算F1消息接受到，而T1虽然是F1的superT
 * 但是不能任务T1也接受到了。原因是，注册T1时，希望获得的是T1消息，而非作为F1的
 * superT来处理的。因此处理消息的时候，要注意，先给处理S，然后F，最后T。同时
 * 处理过程中判断是否满足当前环境。
 *
 * @author Connery
 * @since 4.0
 */
public interface ITriggerThreshold {
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
     * 默认添加第一个And条件集合中。
     *
     * @param statusTag 主题
     */
    void addAndStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException;


    /**
     * 添加一个状态，与此函数添加的全部状态是与关系
     *
     * @param conditionIndex 在指定的ConditionItem下添加条件
     * @param statusTag      主题
     */
    void addAndStatus(Integer conditionIndex, IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个主题，与此函数添加的主题是与关系
     * <p/>
     *
     * @param conditionIndex 在指定的ConditionItem下添加条件
     * @param topicTag       主题
     */
    void addAndTopic(Integer conditionIndex, ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException;

    /**
     * 添加一个分片，与此函数添加的分片是与关系
     *
     * @param conditionIndex 在指定的ConditionItem下添加条件
     * @param fragmentTag    主题
     */
    void addAndFragment(Integer conditionIndex, IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException;

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

    boolean isMeetThreshold();

    BITriggerThreshold.ConditionAndSet getMeetCondition() throws BIThresholdUnsatisfiedException;
}
