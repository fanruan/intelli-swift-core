package com.finebi.cube.impl.router.status;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.pubsub.ISubscribeContainer;
import com.finebi.cube.router.status.IStatus;
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
        , implement = IStatus.class)
public class BIStatus implements IStatus {
    private ISubscribeContainer subscribeContainer;
    private IStatusTag statusTag;

    public BIStatus(IStatusTag statusTag) {
        this.statusTag = statusTag;
        subscribeContainer = BIFactoryHelper.getObject(ISubscribeContainer.class);
    }

    @Override

    public IStatusTag getStatusTag() {
        return statusTag;
    }

    @Override
    public Collection<ISubscribe> getAllSubscribes() {
        return subscribeContainer.getAllSubscribes();
    }

    @Override
    public void addStatusSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException {
        subscribeContainer.addSubscribe(subscribe);
    }

    @Override
    public void removeStatusSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException {
        subscribeContainer.removeSubscribe(subscribe);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message.isStatusMessage()) {
            subscribeContainer.deliverMessage(message);
        }
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe) {
        return subscribeContainer.contain(subscribe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIStatus)) return false;

        BIStatus biStatus = (BIStatus) o;

        if (statusTag != null ? !statusTag.equals(biStatus.statusTag) : biStatus.statusTag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return statusTag != null ? statusTag.hashCode() : 0;
    }
}
