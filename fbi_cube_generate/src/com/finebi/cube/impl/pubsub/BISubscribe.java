package com.finebi.cube.impl.pubsub;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.exception.BIFragmentAbsentException;
import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.exception.BIRegisterIsForbiddenException;
import com.finebi.cube.exception.BIStatusAbsentException;
import com.finebi.cube.exception.BIStatusDuplicateException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.exception.BIThresholdIsOffException;
import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.exception.BITopicDuplicateException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.pubsub.ISubscribeID;
import com.finebi.cube.pubsub.ITrigger;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.manager.PerformancePlugManager;
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
    private boolean verbose = PerformancePlugManager.getInstance().verboseLog();

    private static BILogger LOGGER = BILoggerFactory.getLogger(BISubscribe.class);

    public BISubscribe(ISubscribeID subscribeID, IProcessor processor) {
        BINonValueUtils.checkNull(subscribeID);
        this.subscribeID = subscribeID;
        router = BIFactoryHelper.getObject(IRouter.class);
        trigger = BIFactoryHelper.getObject(ITrigger.class, processor);
    }

    public void subscribeRound(int round) {
        trigger.setTriggerCount(round);
    }

    @Override
    public ISubscribeID getSubscribeID() {
        return subscribeID;
    }

    public void closeVerbose() {
        this.verbose = false;
    }

    @Override
    public void handleMessage(IMessage message) {
        try {
            if (verbose) {
                try {
                    LOGGER.info("Sub:" + subscribeID.getIdentityValue() + "\nSub receive:" + message);
                    LOGGER.info("Left condition:\n" + trigger.leftCondition());
                }catch (Exception e){
                    LOGGER.error(e.getMessage(),e);
                }
            }
            trigger.handleMessage(message);
        } catch (BIThresholdIsOffException e) {
            LOGGER.error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl();
        }
    }

    @Override
    public boolean keepSubscribe() {
        return trigger.keepTriggerOn();
    }

    @Override
    public void subscribe(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException, BIRegisterIsForbiddenException {
        ITopicTag superTopicTag = fragmentTag.getSuperTopicTag();
        try {
            trigger.addAndFragment(fragmentTag);
        } catch (BIFragmentDuplicateException ignore) {
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, fragmentTag)) {
            try {
                router.subscribe(this, superTopicTag, fragmentTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
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
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, superFragmentTag, statusTag)) {
            try {
                router.subscribe(this, superTopicTag, superFragmentTag, statusTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void subscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException {
        try {
            trigger.addAndTopic(topicTag);
        } catch (BITopicDuplicateException ignore) {
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, topicTag)) {
            try {
                router.subscribe(this, topicTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void orSubscribe(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException, BIRegisterIsForbiddenException {
        ITopicTag superTopicTag = fragmentTag.getSuperTopicTag();
        try {
            trigger.addOrFragment(fragmentTag);
        } catch (BIFragmentDuplicateException ignore) {
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, fragmentTag)) {
            try {
                router.subscribe(this, superTopicTag, fragmentTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
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
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, superTopicTag, superFragmentTag, statusTag)) {
            try {
                router.subscribe(this, superTopicTag, superFragmentTag, statusTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void orSubscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException {
        try {
            trigger.addOrTopic(topicTag);
        } catch (BITopicDuplicateException ignore) {
            LOGGER.error("ignore", ignore);
        }
        if (!router.isSubscribed(this, topicTag)) {
            try {
                router.subscribe(this, topicTag);
            } catch (BISubscribeDuplicateException e) {
                LOGGER.error(e.getMessage(), e);
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
