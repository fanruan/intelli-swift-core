package com.finebi.cube.impl.router.fragment;

import com.finebi.cube.exception.*;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.pubsub.ISubscribeContainer;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusContainer;
import com.finebi.cube.router.status.IStatusTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;

import java.util.Collection;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IFragment.class)
public class BIFragment implements IFragment {
    private IFragmentTag fragmentTag;
    private ISubscribeContainer subscribeContainer;
    private IStatusContainer statusContainer;

    public BIFragment(IFragmentTag fragmentTag) {
        this.fragmentTag = fragmentTag;
        subscribeContainer = BIFactoryHelper.getObject(ISubscribeContainer.class);
        statusContainer = BIFactoryHelper.getObject(IStatusContainer.class);
        registerBasicStatus();
    }

    private void registerBasicStatus() {
        try {
            registerStatus(BIFactoryHelper.getObject(IStatus.class, BIStatusTag.getFinishStatusTag(fragmentTag)));
            registerStatus(BIFactoryHelper.getObject(IStatus.class, BIStatusTag.getRunningStatusTag(fragmentTag)));
            registerStatus(BIFactoryHelper.getObject(IStatus.class, BIStatusTag.getStopStatusTag(fragmentTag)));
            registerStatus(BIFactoryHelper.getObject(IStatus.class, BIStatusTag.getWaitingStatusTag(fragmentTag)));

        } catch (BIStatusDuplicateException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public IFragmentTag getFragmentTag() {
        return fragmentTag;
    }

    @Override
    public Collection<ISubscribe> getAllSubscribes() {
        return subscribeContainer.getAllSubscribes();
    }

    @Override
    public void addSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException {
        subscribeContainer.addSubscribe(subscribe);
    }

    @Override
    public void removeSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException {
        subscribeContainer.removeSubscribe(subscribe);
    }

    @Override
    public IStatus getSpecificStatus(IStatusTag statusTag) throws BIStatusAbsentException {
        return statusContainer.getSpecificStatus(statusTag);
    }

    @Override
    public void registerStatus(IStatus status) throws BIStatusDuplicateException {
        statusContainer.registerStatus(status);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe) {
        return subscribeContainer.contain(subscribe);
    }

    @Override
    public boolean isRegistered(IStatusTag statusTag) {
        return statusContainer.contain(statusTag);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message != null) {
            if (message.isFragmentMessage()) {
                subscribeContainer.deliverMessage(message);
            } else {
                statusContainer.deliverMessage(message);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIFragment)) return false;

        BIFragment that = (BIFragment) o;

        return !(fragmentTag != null ? !fragmentTag.equals(that.fragmentTag) : that.fragmentTag != null);

    }

    @Override
    public int hashCode() {
        return fragmentTag != null ? fragmentTag.hashCode() : 0;
    }
}
