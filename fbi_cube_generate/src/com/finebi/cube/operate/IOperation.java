package com.finebi.cube.operate;

import com.finebi.cube.exception.*;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public interface IOperation<R> {

    void setOperationTopicTag(ITopicTag topicTag);

    void setOperationFragmentTag(IFragmentTag fragmentTag);

    ITopicTag getOperationTopicTag();

    IFragmentTag getOperationFragmentTag();

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

    R getOperationResult();


    boolean isSubscribed(ITopicTag topicTag) throws BITopicAbsentException;

    boolean isSubscribed(IStatusTag statusTag)
            throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException;

    boolean isSubscribed(IFragmentTag fragmentTag)
            throws BITopicAbsentException, BIFragmentAbsentException;

}
