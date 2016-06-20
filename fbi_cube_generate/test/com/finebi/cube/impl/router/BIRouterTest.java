package com.finebi.cube.impl.router;

import com.finebi.cube.exception.*;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageFragment;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.pubsub.BISubscribTestTool;
import com.finebi.cube.impl.pubsub.BISubscribe;
import com.finebi.cube.impl.pubsub.BISubscribeID;
import com.finebi.cube.impl.router.fragment.BIFragment;
import com.finebi.cube.impl.router.fragment.BIFragmentID;
import com.finebi.cube.impl.router.fragment.BIFragmentTag;
import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.impl.router.topic.BITopicID;
import com.finebi.cube.impl.router.topic.BITopicTag;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.finebi.cube.pubsub.ISubscribe;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRouterTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IRouter router = BIFactoryHelper.getObject(IRouter.class);
        router.reset();
    }

    public void testRegisterTopic() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = new BITopicTag(new BITopicID("generateCube"));
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testSubscribeTopic() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = BITopicTagTestTool.getTopicTagA();
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
            BISubscribe4Test subscribe = new BISubscribe4Test(new BISubscribeID("sub_1"));
            router.subscribe(subscribe, topicTag);
            router.deliverMessage(BIMessageTestTool.generateMessageTa());
            Thread.sleep(10);
            assertTrue(subscribe.receiveMessage);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }


    public void testRegisterFragment() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = BITopicTagTestTool.getTopicTagA();
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
            IFragment fragment = new BIFragment(BIFragmentTagTestTool.getFragmentTagA());
            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    /**
     * 测试如果没有首先注册topic，抛错
     */
    public void testRegisterFragmentException() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            IFragment fragment = BIFactoryHelper.getObject(IFragment.class, BIFragmentTagTestTool.getFragmentTagA());
            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment);

        } catch (BITopicAbsentException e) {
            return;
        } catch (BIFragmentDuplicateException e) {
            assertFalse(true);
        }
        assertFalse(true);

    }

    /**
     * 注册多个，抛错
     */
    public void testRegisterDoubleFragment() {
        try {
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = BITopicTagTestTool.getTopicTagA();
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
            IFragment fragment = BIFactoryHelper.getObject(IFragment.class, BIFragmentTagTestTool.getFragmentTagA());
            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment);
            IFragment fragment_2 = BIFactoryHelper.getObject(IFragment.class, BIFragmentTagTestTool.getFragmentTagA());

            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment_2);


        } catch (BITopicAbsentException e) {
            assertFalse(true);
        } catch (BIFragmentDuplicateException e) {
            return;
        } catch (BITopicDuplicateException e) {
            assertFalse(true);
        }
        assertFalse(true);

    }


    /**
     * 测试订阅消息的分片
     */
    public void testSubscribeFragment() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = BITopicTagTestTool.getTopicTagA();
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
            IFragment fragment = BIFactoryHelper.getObject(IFragment.class, BIFragmentTagTestTool.getFragmentTagA());
            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment);
            BISubscribe4Test subscribe_A = BISubscribTestTool.generateSubA();
            BISubscribe4Test subscribe_B = BISubscribTestTool.generateSubB();

            router.subscribe(subscribe_A, BITopicTagTestTool.getTopicTagA());
            router.subscribe(subscribe_B, BITopicTagTestTool.getTopicTagA(), BIFragmentTagTestTool.getFragmentTagA());
            /**
             * 发一个A主题的，A分片的消息
             */
            router.deliverMessage(BIMessageTestTool.generateMessageTaFa());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(!subscribe_A.receiveMessage);
            assertTrue(subscribe_B.receiveMessage);
            subscribe_A.resetAlarm();
            subscribe_B.resetAlarm();
            assertFalse(subscribe_A.receiveMessage);
            assertFalse(subscribe_B.receiveMessage);
            /**
             * 发一个A主题的消息
             */
            router.deliverMessage(BIMessageTestTool.generateMessageTa());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(subscribe_A.receiveMessage);
            assertFalse(subscribe_B.receiveMessage);
            /**
             * 随便发个消息
             */try {
                router.deliverMessage(BIMessageTestTool.generateMessageTb());
            } catch (BIDeliverFailureException e) {
                assertTrue(false);
            }


        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testSubscribeStatus() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            ITopicTag topicTag = BITopicTagTestTool.getTopicTagA();
            ITopic topic = BIFactoryHelper.getObject(ITopic.class, topicTag);
            router.registerTopic(topic);
            IFragment fragment = BIFactoryHelper.getObject(IFragment.class, BIFragmentTagTestTool.getFragmentTagA());
            router.registerFragment(BITopicTagTestTool.getTopicTagA(), fragment);
            BISubscribe4Test subscribe_Topic = BISubscribTestTool.generateSubA();
            BISubscribe4Test subscribe_Fragment = BISubscribTestTool.generateSubB();
            BISubscribe4Test subscribe_Status = BISubscribTestTool.generateSubC();

            router.subscribe(subscribe_Topic, BITopicTagTestTool.getTopicTagA());
            router.subscribe(subscribe_Fragment, BITopicTagTestTool.getTopicTagA(), BIFragmentTagTestTool.getFragmentTagA());
            router.subscribe(subscribe_Status, BITopicTagTestTool.getTopicTagA(), BIFragmentTagTestTool.getFragmentTagA(), BIStatusTag.getStopStatusTag(BIFragmentTagTestTool.getFragmentTagA()));
            /**
             * 发一个A主题的，A分片的消息,Stop状态
             */
            router.deliverMessage(BIMessageTestTool.generateMessageTaFaSStop());
            Thread.sleep(10);
            assertFalse(subscribe_Topic.receiveMessage);
            assertFalse(subscribe_Fragment.receiveMessage);
            assertTrue(subscribe_Status.receiveMessage);
            subscribe_Topic.resetAlarm();
            subscribe_Fragment.resetAlarm();
            assertFalse(subscribe_Topic.receiveMessage);
            assertFalse(subscribe_Fragment.receiveMessage);
            /**
             * 发一个A主题的消息
             */
            router.deliverMessage(BIMessageTestTool.generateMessageTa());
            Thread.sleep(10);

            assertTrue(subscribe_Topic.receiveMessage);
            assertFalse(subscribe_Fragment.receiveMessage);
            /**
             * 随便发个消息
             */
//            try {
//                router.deliverMessage(BIMessageTestTool.generateMessageTb());
//            } catch (Exception e) {
//                return ;
//            }
//            assertFalse(true);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    private class BIProcessorCount implements IProcessor {
        public int count;

        @Override
        public void process(IMessage lastReceiveMessage) {
            synchronized (this) {
                count++;
            }
        }

        @Override
        public void setPublish(IPublish publish) {

        }

        @Override
        public Object getResult() {
            return null;
        }
    }

    private class BIProcessorSend implements IProcessor {
        public int count;
        IRouter router = BIFactoryHelper.getObject(IRouter.class);

        @Override
        public void process(IMessage lastReceiveMessage) {
            synchronized (this) {
                count++;
            }
            IFragmentTag fTag = new BIFragmentTag(new BIFragmentID("A"), lastReceiveMessage.getTopic().getTopicTag());
            try {
                router.deliverMessage(new BIMessage(new BIMessageTopic(fTag.getSuperTopicTag()), new BIMessageFragment(fTag), null, null));
            } catch (BIDeliverFailureException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setPublish(IPublish publish) {

        }

        @Override
        public Object getResult() {
            return null;
        }
    }

    /**
     * 单线程发送消息，多个消息接收者
     */
    public void testSubscribeSMultiThread() {
        IRouter router = BIFactoryHelper.getObject(IRouter.class);
        router.setMessageDispatcher(new BIOneThreadDispatcher());
        router.closeVerbose();
        try {
            BIProcessorCount processorOne = new BIProcessorCount();
            BIProcessorCount processorTwo = new BIProcessorCount();

            final ISubscribe subscribeOne = new BISubscribe(new BISubscribeID("one"), processorOne);
            final ISubscribe subscribeTwo = new BISubscribe(new BISubscribeID("two"), processorTwo);
            final Collection<ITopicTag> topicTags = generateTopicTag();

            for (ITopicTag topicTag : topicTags) {
                try {
                    router.registerTopic(topicTag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            subscribeOne.subscribeRound(Integer.MAX_VALUE);
            subscribeTwo.subscribeRound(Integer.MAX_VALUE);
            subscribeOne.closeVerbose();
            subscribeTwo.closeVerbose();
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future<Object> fOne = getSubmit(subscribeOne, topicTags, executorService);
            Future<Object> fTwo = getSubmit(subscribeTwo, topicTags, executorService);
            fOne.get();
            fTwo.get();
            for (ITopicTag topicTag : topicTags) {
                router.deliverMessage(new BIMessage(new BIMessageTopic(topicTag), null, null, null));
            }
            while (!router.isDelivered()) {
                Thread.sleep(10);
            }

            assertEquals(processorOne.count, topicTags.size());
            assertEquals(processorTwo.count, topicTags.size());

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private Future<Object> getSubmit(final ISubscribe subscribeOne, final Collection<ITopicTag> topicTags, ExecutorService executorService) {
        return executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (ITopicTag topicTag : topicTags) {
                    try {
                        subscribeOne.orSubscribe(topicTag);
                    } catch (BITopicAbsentException e) {
                        e.printStackTrace();
                    } catch (BIRegisterIsForbiddenException e) {
                        e.printStackTrace();
                    }
                }
                return new Object();
            }
        });
    }

    /**
     * 多个线程发送消息，
     */
    public void testMultiThreadMultiSent() {
        final IRouter router = BIFactoryHelper.getObject(IRouter.class);
        router.setMessageDispatcher(new BIOneThreadDispatcher());
        router.closeVerbose();
        try {
            BIProcessorCount processorOne = new BIProcessorCount();
            BIProcessorCount processorTwo = new BIProcessorCount();

            final ISubscribe subscribeOne = new BISubscribe(new BISubscribeID("one"), processorOne);
            final ISubscribe subscribeTwo = new BISubscribe(new BISubscribeID("two"), processorTwo);
            final Collection<ITopicTag> topicTags = generateTopicTag();

            for (ITopicTag topicTag : topicTags) {
                try {
                    router.registerTopic(topicTag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            subscribeOne.subscribeRound(Integer.MAX_VALUE);
            subscribeTwo.subscribeRound(Integer.MAX_VALUE);
            subscribeOne.closeVerbose();
            subscribeTwo.closeVerbose();
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future<Object> fOne = getSubmit(subscribeOne, topicTags, executorService);
            Future<Object> fTwo = getSubmit(subscribeTwo, topicTags, executorService);
            fOne.get();
            fTwo.get();

            Future<Object> deOne = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (ITopicTag topicTag : topicTags) {
                        router.deliverMessage(new BIMessage(new BIMessageTopic(topicTag), null, null, null));
                    }
                    return new Object();
                }
            });
            Future<Object> deTwo = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (ITopicTag topicTag : topicTags) {
                        router.deliverMessage(new BIMessage(new BIMessageTopic(topicTag), null, null, null));
                    }
                    return new Object();
                }
            });
            deOne.get();
            deTwo.get();
            while (!router.isDelivered()) {
                Thread.sleep(10);
            }
            assertEquals(processorOne.count, topicTags.size() * 2);
            assertEquals(processorTwo.count, topicTags.size() * 2);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * 多个线程发送消息，
     * 同时一个接受者A接受到消息后，会发送一个消息。
     * 另一个接收者会接受A发来的消息。
     */
    public void testMultiThreadSubSend() {
        final IRouter router = BIFactoryHelper.getObject(IRouter.class);
//        router.setMessageDispatcher(new BIOneThreadDispatcher());
        router.closeVerbose();
        try {
            BIProcessorCount processorReceiver = new BIProcessorCount();
            BIProcessorSend processorSender = new BIProcessorSend();

            final ISubscribe subscribeReceiver = new BISubscribe(new BISubscribeID("one"), processorReceiver);
            final ISubscribe subscribeSender = new BISubscribe(new BISubscribeID("two"), processorSender);

            final Collection<ITopicTag> topicTags = generateTopicTag();
            final Collection<IFragmentTag> fragmentTags = generateFragmentTag(topicTags);

            for (ITopicTag topicTag : topicTags) {
                try {
                    router.registerTopic(topicTag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (IFragmentTag fragmentTag : fragmentTags) {
                try {
                    router.registerFragment(fragmentTag.getSuperTopicTag(), fragmentTag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            subscribeReceiver.subscribeRound(Integer.MAX_VALUE);
            subscribeSender.subscribeRound(Integer.MAX_VALUE);
            subscribeReceiver.closeVerbose();
            subscribeSender.closeVerbose();
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future<Object> fOne = getSubmit(subscribeReceiver, topicTags, executorService);
            Future<Object> fTwo = getSubmit(subscribeSender, topicTags, executorService);

            Future<Object> oneSubFrag = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (IFragmentTag fragmentTag : fragmentTags) {
                        try {
                            subscribeReceiver.orSubscribe(fragmentTag);
                        } catch (BITopicAbsentException e) {
                            e.printStackTrace();
                        } catch (BIRegisterIsForbiddenException e) {
                            e.printStackTrace();
                        }
                    }
                    return new Object();
                }
            });
            fOne.get();
            fTwo.get();
            oneSubFrag.get();
            Future<Object> deOne = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (ITopicTag topicTag : topicTags) {
                        router.deliverMessage(new BIMessage(new BIMessageTopic(topicTag), null, null, null));
                    }
                    return new Object();
                }
            });
            Future<Object> deTwo = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (ITopicTag topicTag : topicTags) {
                        router.deliverMessage(new BIMessage(new BIMessageTopic(topicTag), null, null, null));
                    }
                    return new Object();
                }
            });
            deOne.get();
            deTwo.get();
            while (!router.isDelivered()) {
                Thread.sleep(10);
            }
            Thread.sleep(1000);
            assertEquals(topicTags.size() * 4, processorReceiver.count);
            assertEquals(topicTags.size() * 2, processorSender.count);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private Collection<ITopicTag> generateTopicTag() {
        Collection<ITopicTag> result = new HashSet<ITopicTag>();
//        int time = Math.abs(BIRandomUitils.getRandomInteger() % 8);
        int time = 100;

        for (int i = 0; i < time; i++) {
            String topicID = BIRandomUitils.getRandomCharacterString(10);
            ITopicTag topicTag = new BITopicTag(new BITopicID(topicID));
            if (!result.contains(topicTag)) {
                result.add(topicTag);
            }
        }
        return result;
    }

    private Collection<IFragmentTag> generateFragmentTag(Collection<ITopicTag> topics) {
        Collection<IFragmentTag> result = new HashSet<IFragmentTag>();
        for (ITopicTag topic : topics) {
            result.add(new BIFragmentTag(new BIFragmentID("A"), topic));
        }
        return result;
    }
}
