package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ITriggerThreshold;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
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
        , implement = ITriggerThreshold.class)
public class BITriggerThreshold implements ITriggerThreshold {
    private BITopicTagThreshold topicTagThreshold;
    private BIFragmentTagThreshold fragmentTagThreshold;
    private BIStatusTagThreshold statusTagThreshold;

    public BITriggerThreshold() {
        topicTagThreshold = new BITopicTagThreshold();
        fragmentTagThreshold = new BIFragmentTagThreshold();
        statusTagThreshold = new BIStatusTagThreshold();
    }

    @Override
    public void addAndTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        try {
            topicTagThreshold.registerThresholdTag(topicTag);
        } catch (BITagDuplicateException e) {
            throw new BITopicDuplicateException(e.getMessage(), e);
        }
    }

    @Override
    public void addAndFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        try {
            fragmentTagThreshold.registerThresholdTag(fragmentTag);
        } catch (BITagDuplicateException e) {
            throw new BIFragmentDuplicateException(e.getMessage(), e);
        }
    }

    @Override
    public void addAndStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        try {
            statusTagThreshold.registerThresholdTag(statusTag);
        } catch (BITagDuplicateException e) {
            throw new BIStatusDuplicateException(e.getMessage(), e);
        }
    }

    @Override
    public void addOrTopic(ITopicTag topicTag) {

    }

    @Override
    public void addOrFragment(IFragmentTag fragmentTag) {

    }

    @Override
    public void addOrStatus(IStatusTag statusTag) {

    }

    @Override
    public void handleMessage(IMessage message) throws BIThresholdIsOffException {
        if (message.isTopicMessage()) {
            topicTagThreshold.handleMessage(message);
        } else if (message.isFragmentMessage()) {
            fragmentTagThreshold.handleMessage(message);
        } else if (message.isStatusMessage()) {
            statusTagThreshold.handleMessage(message);
        }
    }

    private boolean isTopicMeetThreshold() {
        try {
            return topicTagThreshold.isMeetThreshold();
        } catch (BIThresholdIsOffException e) {
            /**
             * 没有开启，那么说明没有监控。
             */
            return true;
        }
    }

    private boolean isFragmentMeetThreshold() {
        try {
            return fragmentTagThreshold.isMeetThreshold();
        } catch (BIThresholdIsOffException e) {
            /**
             * 没有开启，那么说明没有监控。
             */
            return true;
        }
    }

    private boolean isStatusMeetThreshold() {
        try {
            return statusTagThreshold.isMeetThreshold();
        } catch (BIThresholdIsOffException e) {
            /**
             * 没有开启，那么说明没有监控。
             */
            return true;
        }
    }

    @Override
    public boolean isMeetThreshold() {
        return isStatusMeetThreshold() &&
                isFragmentMeetThreshold() &&
                isTopicMeetThreshold();
    }
}
