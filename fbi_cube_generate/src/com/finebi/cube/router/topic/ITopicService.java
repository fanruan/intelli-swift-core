package com.finebi.cube.router.topic;

import com.finebi.cube.exception.BIFragmentAbsentException;
import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;

import java.util.Collection;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface ITopicService {

    Collection<ISubscribe> getAllSubscribes();

    Collection<IFragment> getAllFragments();

    void addTopicSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException;

    void removeTopicSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException;

    void registerFragment(IFragment fragment) throws BIFragmentDuplicateException;

    IFragment getSpecificFragment(IFragmentTag fragmentTag) throws BIFragmentAbsentException;

    boolean isSubscribed(ISubscribe subscribe);

    boolean isRegistered(IFragmentTag fragmentTag);
}
