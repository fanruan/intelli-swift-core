package com.finebi.cube.router.status;

import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.pubsub.ISubscribe;

import java.util.Collection;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IStatusService {

    Collection<ISubscribe> getAllSubscribes();

    void addStatusSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException;

    void removeStatusSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException;

    boolean isSubscribed(ISubscribe subscribe);

}
