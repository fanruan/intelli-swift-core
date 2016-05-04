package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ITriggerThreshold;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.*;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ITriggerThreshold.class)
public class BITriggerThreshold extends BIMapContainer<Integer, BITriggerThreshold.ConditionAndSet> implements ITriggerThreshold {
    @Override
    protected Map<Integer, ConditionAndSet> initContainer() {
        return new HashMap<Integer, ConditionAndSet>();
    }

    @Override
    protected ConditionAndSet generateAbsentValue(Integer key) {
        return new ConditionAndSet();
    }

    public BITriggerThreshold() {
        try {
            getValue(0);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private ConditionAndSet getAndSet(Integer index) {
        try {
            return getValue(index);
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void addAndTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        addAndTopic(0, topicTag);
    }

    @Override
    public void addAndFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        addAndFragment(0, fragmentTag);
    }

    @Override
    public void addAndStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        addAndStatus(0, statusTag);
    }

    @Override
    public void addAndStatus(Integer conditionIndex, IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        checkIndex(conditionIndex);
        try {
            getAndSet(conditionIndex).getStatusTagThreshold().registerThresholdTag(statusTag);
        } catch (BITagDuplicateException e) {
            throw new BIStatusDuplicateException(e.getMessage(), e);
        }
    }

    private void checkIndex(Integer conditionIndex) {
        if (conditionIndex > container.size()) {
            throw BINonValueUtils.beyondControl("The condition index:" + container.size() + " is in vain,so the parameter :" + conditionIndex + " is illegal");
        } else if (conditionIndex < 0) {
            throw BINonValueUtils.beyondControl("The condition index should be positive");

        }
    }


    @Override
    public void addAndTopic(Integer conditionIndex, ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        checkIndex(conditionIndex);
        try {
            getAndSet(conditionIndex).getTopicTagThreshold().registerThresholdTag(topicTag);
        } catch (BITagDuplicateException e) {
            throw new BITopicDuplicateException(e.getMessage(), e);
        }
    }

    @Override
    public void addAndFragment(Integer conditionIndex, IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        checkIndex(conditionIndex);
        try {
            getAndSet(conditionIndex).getFragmentTagThreshold().registerThresholdTag(fragmentTag);
        } catch (BITagDuplicateException e) {
            throw new BIFragmentDuplicateException(e.getMessage(), e);
        }
    }

    private Integer getVainConditionIndex() {
        return container.size();
    }

    @Override
    public void addOrTopic(ITopicTag topicTag) throws BITopicDuplicateException, BIRegisterIsForbiddenException {
        addAndTopic(getVainConditionIndex(), topicTag);
    }

    @Override
    public void addOrFragment(IFragmentTag fragmentTag) throws BIFragmentDuplicateException, BIRegisterIsForbiddenException {
        addAndFragment(getVainConditionIndex(), fragmentTag);
    }

    @Override
    public void addOrStatus(IStatusTag statusTag) throws BIStatusDuplicateException, BIRegisterIsForbiddenException {
        addAndStatus(getVainConditionIndex(), statusTag);
    }

    public int conditionItemSize() {
        return container.size();
    }

    @Override
    public void handleMessage(IMessage message) throws BIThresholdIsOffException {
        Iterator<ConditionAndSet> it = container.values().iterator();
        while (it.hasNext()) {
            it.next().handleMessage(message);
        }
    }


    @Override
    public boolean isMeetThreshold() {
        try {
            getMeetCondition();
            return true;
        } catch (BIThresholdUnsatisfiedException e) {
            return false;
        }
    }

    public ConditionAndSet getMeetCondition() throws BIThresholdUnsatisfiedException {
        Iterator<ConditionAndSet> it = container.values().iterator();
        while (it.hasNext()) {
            ConditionAndSet condition = it.next();
            /**
             * 如果没有开启，那么认为没有达到。
             */
            if (condition.isUsable() && condition.isMeetThreshold()) {
                return condition;
            }
        }
        throw new BIThresholdUnsatisfiedException();
    }

    public class ConditionAndSet {
        private BITopicTagThreshold topicTagThreshold;
        private BIFragmentTagThreshold fragmentTagThreshold;
        private BIStatusTagThreshold statusTagThreshold;

        public ConditionAndSet() {
            topicTagThreshold = new BITopicTagThreshold();
            fragmentTagThreshold = new BIFragmentTagThreshold();
            statusTagThreshold = new BIStatusTagThreshold();
        }

        public BITopicTagThreshold getTopicTagThreshold() {
            return topicTagThreshold;
        }

        public BIStatusTagThreshold getStatusTagThreshold() {
            return statusTagThreshold;
        }

        public BIFragmentTagThreshold getFragmentTagThreshold() {
            return fragmentTagThreshold;
        }

        public boolean isUsable() {
            return topicTagThreshold.isSwitchOn() || fragmentTagThreshold.isSwitchOn() || statusTagThreshold.isSwitchOn();
        }

        public boolean isTopicMeetThreshold() {
            try {
                return topicTagThreshold.isMeetThreshold();
            } catch (BIThresholdIsOffException e) {
                /**
                 * 没有开启，那么说明没有监控。
                 */
                return true;
            }
        }

        public boolean isFragmentMeetThreshold() {
            try {
                return fragmentTagThreshold.isMeetThreshold();
            } catch (BIThresholdIsOffException e) {
                /**
                 * 没有开启，那么说明没有监控。
                 */
                return true;
            }
        }

        public boolean isStatusMeetThreshold() {
            try {
                return statusTagThreshold.isMeetThreshold();
            } catch (BIThresholdIsOffException e) {
                /**
                 * 没有开启，那么说明没有监控。
                 */
                return true;
            }
        }

        public boolean isMeetThreshold() {
            return isStatusMeetThreshold() &&
                    isFragmentMeetThreshold() &&
                    isTopicMeetThreshold();
        }

        protected void handleMessage(IMessage message) throws BIThresholdIsOffException {
            try {
                if (message.isTopicMessage()) {
                    topicTagThreshold.handleMessage(message);
                } else if (message.isFragmentMessage()) {
                    fragmentTagThreshold.handleMessage(message);
                } else if (message.isStatusMessage()) {
                    statusTagThreshold.handleMessage(message);
                }
            } catch (BIThresholdIsOffException e) {
                return;
            }
        }
    }
}
