package com.finebi.cube.pubsub;

import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.router.IMessageDeliver;

import java.util.Collection;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface ISubscribeContainer extends IMessageDeliver {
    Collection<ISubscribe> getAllSubscribes();

    void addSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException;

    void removeSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException;

    boolean contain(ISubscribe subscribe);
}
