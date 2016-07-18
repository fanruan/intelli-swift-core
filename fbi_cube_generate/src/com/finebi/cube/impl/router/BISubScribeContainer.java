package com.finebi.cube.impl.router;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.exception.BISubscribeAbsentException;
import com.finebi.cube.exception.BISubscribeDuplicateException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.pubsub.ISubscribeContainer;
import com.finebi.cube.pubsub.ISubscribeID;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.program.BICollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = ISubscribeContainer.class)
public class BISubScribeContainer extends BIMapContainer<ISubscribeID, ISubscribe> implements ISubscribeContainer {

    @Override
    protected Map<ISubscribeID, ISubscribe> initContainer() {
        return new HashMap<ISubscribeID, ISubscribe>();
    }

    @Override
    protected ISubscribe generateAbsentValue(ISubscribeID key) {
        return null;
    }

    @Override
    public Collection<ISubscribe> getAllSubscribes() {
        synchronized (container) {
            return BICollectionUtils.unmodifiedCollection(container.values());
        }
    }

    @Override
    public void addSubscribe(ISubscribe subscribe) throws BISubscribeDuplicateException {
        try {
            synchronized (container) {
                putKeyValue(subscribe.getSubscribeID(), subscribe);
            }
        } catch (BIKeyDuplicateException ignore) {
            throw new BISubscribeDuplicateException();
        }
    }

    @Override
    public void removeSubscribe(ISubscribe subscribe) throws BISubscribeAbsentException {
        try {
            remove(subscribe.getSubscribeID());
        } catch (BIKeyAbsentException ignore) {
            throw new BISubscribeAbsentException();
        }
    }

    @Override
    public boolean contain(ISubscribe subscribe) {
        return containsKey(subscribe.getSubscribeID());
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        synchronized (container) {
            Iterator<ISubscribe> it = container.values().iterator();
            while (it.hasNext()) {
                ISubscribe subscribe = it.next();
//                if (ComparatorUtils.equals(subscribe.getSubscribeID().getIdentityValue(), "0a9c656c")) {
//                    if (message.getFragment() != null && message.getFragment().getFragmentTag().getFragmentID().getIdentityValue().equals("idA__fineBI_sub_empty"))
//                    if (message.getFragment() != null && message.getFragment().getFragmentTag().getFragmentID().getIdentityValue().equals("A_name__fineBI_sub_empty"))
//                        System.out.println("find");
//                }
                handleMessage(subscribe, message);
                if (!subscribe.keepSubscribe()) {
                    it.remove();
                }
            }
        }
    }


    /**
     * todo 防止subscribe处理消息卡死。
     *
     * @param subscribe
     * @param message
     */
    private void handleMessage(ISubscribe subscribe, IMessage message) {
        subscribe.handleMessage(message);
    }
}
