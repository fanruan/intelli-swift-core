package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.*;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ISubscribe.class)
public class BISubscribe implements ISubscribe {
    private ISubscribeID subscribeID;
    private IRouter router;
    private ITrigger trigger;

    public BISubscribe(ISubscribeID subscribeID, IProcessor processor) {
        BINonValueUtils.checkNull(subscribeID);
        this.subscribeID = subscribeID;
        router = BIFactoryHelper.getObject(IRouter.class);
        trigger = BIFactoryHelper.getObject(ITrigger.class, processor);
    }


    @Override
    public ISubscribeID getSubscribeID() {
        return subscribeID;
    }

    @Override
    public void handleMessage(IMessage message) {
        try {
            trigger.handleMessage(message);
        } catch (BIThresholdIsOffException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl();
        }
    }

    @Override
    public void subscribe(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException, BIRegisterIsForbiddenException {
        ITopicTag superTopicTag = fragmentTag.getSuperTopicTag();
        try {
            trigger.addAndFragment(fragmentTag);
        } catch (BIFragmentDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, fragmentTag)) {
            try {
                router.subscribe(this, superTopicTag, fragmentTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }

    }

    @Override
    public void subscribe(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException,
            BIStatusAbsentException, BIRegisterIsForbiddenException {
        IFragmentTag superFragmentTag = statusTag.getSuperFragmentTag();
        ITopicTag superTopicTag = superFragmentTag.getSuperTopicTag();
        try {
            trigger.addAndStatus(statusTag);
        } catch (BIStatusDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, superFragmentTag, statusTag)) {
            try {
                router.subscribe(this, superTopicTag, superFragmentTag, statusTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void subscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException {
        try {
            trigger.addAndTopic(topicTag);
        } catch (BITopicDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, topicTag)) {
            try {
                router.subscribe(this, topicTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void orSubscribe(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException, BIRegisterIsForbiddenException {
        ITopicTag superTopicTag = fragmentTag.getSuperTopicTag();
        try {
            trigger.addOrFragment(fragmentTag);
        } catch (BIFragmentDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, fragmentTag)) {
            try {
                router.subscribe(this, superTopicTag, fragmentTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }

    }

    @Override
    public void orSubscribe(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException,
            BIStatusAbsentException, BIRegisterIsForbiddenException {
        IFragmentTag superFragmentTag = statusTag.getSuperFragmentTag();
        ITopicTag superTopicTag = superFragmentTag.getSuperTopicTag();
        try {
            trigger.addOrStatus(statusTag);
        } catch (BIStatusDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, superFragmentTag, statusTag)) {
            try {
                router.subscribe(this, superTopicTag, superFragmentTag, statusTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void orSubscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException {
        try {
            trigger.addOrTopic(topicTag);
        } catch (BITopicDuplicateException ignore) {
            BILogger.getLogger().error("ignore", ignore);
        }
        if (!router.isSubscribed(this, topicTag)) {
            try {
                router.subscribe(this, topicTag);
            } catch (BISubscribeDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }
    @Override
    public boolean isSubscribed(ITopicTag topicTag) throws BITopicAbsentException {
        return router.isSubscribed(this, topicTag);
    }

    @Override
    public boolean isSubscribed(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException {
        IFragmentTag superFragmentTag = statusTag.getSuperFragmentTag();
        return router.isSubscribed(this, superFragmentTag.getSuperTopicTag(), superFragmentTag, statusTag);
    }

    @Override
    public boolean isSubscribed(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException {
        return router.isSubscribed(this, fragmentTag.getSuperTopicTag(), fragmentTag);
    }
}
