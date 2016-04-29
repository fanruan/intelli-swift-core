package com.finebi.cube.impl.router.topic;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.pubsub.ISubscribeContainer;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentContainer;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;

import java.util.Collection;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ITopic.class)
public class BITopic implements ITopic {
    private ITopicTag topicTag;
    private ISubscribeContainer subscribeContainer;
    private IFragmentContainer fragmentContainer;

    public BITopic(ITopicTag topicTag) {
        this.topicTag = topicTag;
        subscribeContainer = BIFactoryHelper.getObject(ISubscribeContainer.class);
        fragmentContainer = BIFactoryHelper.getObject(IFragmentContainer.class);
    }

    @Override
    public ITopicTag getTopicTag() {
        return topicTag;
    }

    @Override
    public Collection<ISubscribe> getAllSubscribes() {
        return subscribeContainer.getAllSubscribes();
    }

    @Override
    public Collection<IFragment> getAllFragments() {
        return fragmentContainer.getAllFragments();
    }

    @Override
    public void addTopicSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException {
        subscribeContainer.addSubscribe(subscribe);
    }

    @Override
    public void removeTopicSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException {
        subscribeContainer.removeSubscribe(subscribe);
    }

    @Override
    public void registerFragment(IFragment fragment) throws BIFragmentDuplicateException {
        fragmentContainer.registerFragment(fragment);
    }

    @Override
    public IFragment getSpecificFragment(IFragmentTag fragmentTag) throws BIFragmentAbsentException {
        return fragmentContainer.getSpecificFragment(fragmentTag);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message != null && message.getTopic() != null) {
            if (message.isTopicMessage()) {
                subscribeContainer.deliverMessage(message);
            } else {
                fragmentContainer.deliverMessage(message);
            }
        }
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe) {
        return subscribeContainer.contain(subscribe);
    }

    @Override
    public boolean isRegistered(IFragmentTag fragmentTag) {
        return fragmentContainer.contain(fragmentTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BITopic)) return false;

        BITopic biTopic = (BITopic) o;

        return !(topicTag != null ? !topicTag.equals(biTopic.topicTag) : biTopic.topicTag != null);

    }

    @Override
    public int hashCode() {
        return topicTag != null ? topicTag.hashCode() : 0;
    }

}
