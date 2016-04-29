package com.finebi.cube.router.topic;

import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.exception.BITopicDuplicateException;
import com.finebi.cube.router.IMessageDeliver;

import java.util.Collection;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface ITopicContainer extends IMessageDeliver {
    Collection<ITopic> getAllTopics();

    void registerTopic(ITopic topic) throws BITopicDuplicateException;

    ITopic getSpecificTopic(ITopicTag topicTag) throws BITopicAbsentException;

    void clear();

    boolean contain(ITopicTag topicTag);
}
