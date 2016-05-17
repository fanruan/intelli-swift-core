package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.ITrigger;
import com.finebi.cube.pubsub.ITriggerThreshold;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ITrigger.class)
public class BITrigger implements ITrigger {
    private ITriggerThreshold threshold;
    private IProcessor processor;
    private int triggerCount;

    public BITrigger(IProcessor processor) {
        this.processor = processor;
        threshold = BIFactoryHelper.getObject(ITriggerThreshold.class);
        triggerCount = 1;
    }

    @Override
    public ITriggerThreshold getThreshold() {
        return threshold;
    }

    @Override
    public void setTriggerCount(int count) {
        synchronized (this) {
            if (count > 0) {
                this.triggerCount = count;
            }
        }
    }

    @Override
    public boolean keepTriggerOn() {
        return triggerCount > 0;
    }

    private void triggerOne() {
        synchronized (this) {
            if (triggerCount != Integer.MAX_VALUE) {
                triggerCount--;
            }
        }
    }

    @Override
    public void addAndTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        threshold.addAndTopic(topicTag);
    }

    @Override
    public void addAndFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        threshold.addAndFragment(fragmentTag);
    }

    @Override
    public void addAndStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        threshold.addAndStatus(statusTag);
    }

    @Override
    public void addOrTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        threshold.addOrTopic(topicTag);
    }

    @Override
    public void addOrFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        threshold.addAndFragment(fragmentTag);
    }

    @Override
    public void addOrStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        threshold.addOrStatus(statusTag);
    }

    @Override
    public void handleMessage(IMessage message) throws BIThresholdIsOffException {
        threshold.handleMessage(message);
        if (threshold.isMeetThreshold()) {
            processor.process(message);
            triggerOne();
        }
    }

    @Override
    public String leftCondition() {
        return threshold.leftCondition();
    }
}
