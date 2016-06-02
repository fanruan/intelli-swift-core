package com.finebi.cube.impl.router;

import com.finebi.cube.exception.*;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicRouterService;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.factory.annotation.BISingletonObject;
import com.fr.bi.stable.utils.code.BILogger;


/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IRouter.class)
@BISingletonObject
public class BIRouter implements IRouter {
    private ITopicRouterService topicRouterService;
    private BIMessageDispatcher messageDispatcher;
    private boolean verbose = true;
    private Thread dispatcherThread;

    public BIRouter() {
        this(new BIMessageDispatcher());
    }

    public BIRouter(BIMessageDispatcher dispatcher) {
        topicRouterService = BIFactoryHelper.getObject(ITopicRouterService.class);
        messageDispatcher = dispatcher;
        openDispatcher();
        monitor();
    }

    public void setMessageDispatcher(BIMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        if (dispatcherThread != null && dispatcherThread.isAlive()) {
            dispatcherThread.interrupt();
        }
        openDispatcher();
    }

    private void openDispatcher() {
        messageDispatcher.setTopicRouterService(topicRouterService);
        dispatcherThread = new Thread(messageDispatcher);
        dispatcherThread.setName("fcube-router");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }

    private void monitor() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("router-monitor");
        thread.start();
    }

    @Override
    public void deliverMessage(IMessage message) throws BIDeliverFailureException {
        if (verbose) {
            BILogger.getLogger().addLog("Message Router Receive:" + message.toString());
            System.out.println("Message Router Receive:" + message.toString());
        }
        messageDispatcher.addMessage(message);
    }


    @Override
    public void registerTopic(ITopic topic) throws BITopicDuplicateException {
        topicRouterService.registerTopic(topic);
    }

    @Override
    public void registerTopic(ITopicTag topicTag) throws BITopicDuplicateException {
        if (!isRegistered(topicTag)) {
            registerTopic(BIFactoryHelper.getObject(ITopic.class, topicTag));
        }
    }

    @Override
    public void registerFragment(ITopicTag topicTag, IFragment fragment) throws BITopicAbsentException, BIFragmentDuplicateException {
        topicRouterService.registerFragment(topicTag, fragment);
    }

    @Override
    public void registerFragment(ITopicTag topicTag, IFragmentTag fragment) throws BITopicAbsentException, BIFragmentDuplicateException {
        if (!isRegistered(fragment)) {
            registerFragment(topicTag, BIFactoryHelper.getObject(IFragment.class, fragment));
        }
    }

    @Override
    public void registerStatus(ITopicTag topicTag, IFragmentTag fragmentTag, IStatus status) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusDuplicateException {
        topicRouterService.registerStatus(topicTag, fragmentTag, status);
    }

    @Override
    public IFragment subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException, BISubscribeDuplicateException {
        return topicRouterService.subscribe(subscribe, topicTag, fragmentTag);
    }

    @Override
    public IStatus subscribe(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException, BISubscribeDuplicateException {
        return topicRouterService.subscribe(subscribe, topicTag, fragmentTag, statusTag);
    }

    @Override
    public ITopic subscribe(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException, BISubscribeDuplicateException {
        return topicRouterService.subscribe(subscribe, topicTag);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag) throws BITopicAbsentException {
        return topicRouterService.isSubscribed(subscribe, topicTag);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag, IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException {
        return topicRouterService.isSubscribed(subscribe, topicTag, fragmentTag, statusTag);
    }

    @Override
    public boolean isSubscribed(ISubscribe subscribe, ITopicTag topicTag, IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException {
        return topicRouterService.isSubscribed(subscribe, topicTag, fragmentTag);
    }

    @Override
    public boolean isRegistered(ITopicTag topicTag) {
        return topicRouterService.isRegistered(topicTag);
    }

    @Override
    public boolean isRegistered(IFragmentTag fragmentTag) {
        return topicRouterService.isRegistered(fragmentTag);
    }

    @Override
    public boolean isRegistered(IStatusTag statusTag) {
        return topicRouterService.isRegistered(statusTag);
    }

    @Override
    public void reset() {
        topicRouterService.reset();
    }

    @Override
    public boolean isDelivered() {
        return messageDispatcher.isDispatched();
    }

    @Override
    public void closeVerbose() {
        this.verbose = false;
        this.topicRouterService.closeVerbose();
    }
}
