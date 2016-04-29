package com.finebi.cube.impl.router;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.exception.BITopicDuplicateException;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.pubsub.BISubscribTestTool;
import com.finebi.cube.impl.pubsub.BISubscribeID;
import com.finebi.cube.impl.router.fragment.BIFragment;
import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.impl.router.topic.BITopicID;
import com.finebi.cube.impl.router.topic.BITopicTag;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

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
//            router.deliverMessage(BIMessageTestTool.generateMessageTb());

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }
}
