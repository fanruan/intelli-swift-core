package com.finebi.cube.router.fragment;

import com.finebi.cube.exception.BIStatusAbsentException;
import com.finebi.cube.exception.BIStatusDuplicateException;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;

import java.util.Collection;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IFragmentService {
    Collection<ISubscribe> getAllSubscribes();

    void addSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException;

    void removeSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException;

    IStatus getSpecificStatus(IStatusTag statusTag) throws BIStatusAbsentException;

    void registerStatus(IStatus status) throws BIStatusDuplicateException;

    boolean isSubscribed(ISubscribe subscribe);

    boolean isRegistered(IStatusTag statusTag);

}
