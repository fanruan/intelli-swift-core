package com.finebi.cube.impl.router.topic;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicContainer;
import com.finebi.cube.router.topic.ITopicRouterService;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ITopicRouterService.class)
public class BITopicManager implements ITopicRouterService {
    private ITopicContainer topicContainer;
    private boolean verbose = true;

    public BITopicManager() {
        topicContainer = BIFactoryHelper.getObject(ITopicContainer.class);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        topicContainer.deliverMessage(message);
    }

    @Override
    public void registerTopic(ITopic topic) throws BITopicDuplicateException {
        topicContainer.registerTopic(topic);
    }

    @Override
    public void registerFragment(ITopicTag topicTag, IFragment fragment) throws BITopicAbsentException, BIFragmentDuplicateException {
        topicContainer.getSpecificTopic(topicTag).registerFragment(fragment);
    }

    @Override
    public void registerStatus(ITopicTag topicTag, IFragmentTag fragmentTag, IStatus status) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusDuplicateException {
        topicContainer.getSpecificTopic(topicTag).getSpecificFragment(fragmentTag).registerStatus(status);
    }

    @Override
    public IFragment subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BISubscribeDuplicateException {
        if (verbose) {
            System.out.println("Sub:" + subscribe.getSubscribeID().getIdentityValue() + "\nsubscribe mess: " + "T:" + topicTag + ",F:" + fragmentTag);
            System.out.println("");
        }
        IFragment fragment = getSpecificTopic(topicTag).getSpecificFragment(fragmentTag);
        fragment.addSubscribe(subscribe);
        return fragment;
    }

    @Override
    public IStatus subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag) throws BITopicAbsentException
            , BIFragmentAbsentException, BIStatusAbsentException, BISubscribeDuplicateException {
        if (verbose) {
            System.out.println("Sub:" + subscribe.getSubscribeID().getIdentityValue() + "\nsubscribe mess: " + "T:" + topicTag + ",F:" + fragmentTag + ",S:" + statusTag);
            System.out.println("");
        }
        IStatus status = getSpecificTopic(topicTag).getSpecificFragment(fragmentTag).getSpecificStatus(statusTag);
        status.addStatusSubscribe(subscribe);
        return status;
    }

    @Override
    public ITopic subscribe(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException, BISubscribeDuplicateException {
        if (verbose) {
            System.out.println("Sub:" + subscribe.getSubscribeID().getIdentityValue() + "\nsubscribe mess: " + "T:" + topicTag);
            System.out.println("");
        }
        ITopic topic = getSpecificTopic(topicTag);
        topic.addTopicSubscribe(subscribe);
        return topic;
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException {
        ITopic topic = getSpecificTopic(topicTag);
        return topic.isSubscribed(subscribe);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag)
            throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException {
        ITopic topic = getSpecificTopic(topicTag);
        IFragment fragment = topic.getSpecificFragment(fragmentTag);
        IStatus status = fragment.getSpecificStatus(statusTag);
        return status.isSubscribed(subscribe);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag)
            throws BITopicAbsentException, BIFragmentAbsentException {
        ITopic topic = getSpecificTopic(topicTag);
        IFragment fragment = topic.getSpecificFragment(fragmentTag);
        return fragment.isSubscribed(subscribe);
    }

    @Override
    public boolean isRegistered(ITopicTag topicTag) {
        return topicContainer.contain(topicTag);
    }

    @Override
    public boolean isRegistered(IFragmentTag fragmentTag) {
        if (topicContainer.contain(fragmentTag.getSuperTopicTag())) {
            try {
                ITopic topic = getSpecificTopic(fragmentTag.getSuperTopicTag());
                return topic.isRegistered(fragmentTag);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return false;
    }

    private ITopic getSpecificTopic(ITopicTag topicTag) throws BITopicAbsentException {
        return topicContainer.getSpecificTopic(topicTag);
    }

    @Override
    public boolean isRegistered(IStatusTag statusTag) {
        IFragmentTag fragmentTag = statusTag.getSuperFragmentTag();
        ITopicTag topicTag = fragmentTag.getSuperTopicTag();
        try {
            ITopic topic = getSpecificTopic(topicTag);
            IFragment fragment = topic.getSpecificFragment(fragmentTag);
            return fragment.isRegistered(statusTag);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void reset() {
        topicContainer.clear();
    }

    @Override
    public void closeVerbose() {
        this.verbose = false;
    }
}
