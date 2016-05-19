package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.*;
import com.finebi.cube.impl.message.BIMessageFragment;
import com.finebi.cube.impl.message.BIMessageStatus;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageBody;
import com.finebi.cube.message.IMessageFragment;
import com.finebi.cube.message.IMessageGenerator;
import com.finebi.cube.pubsub.IPublish;
import com.finebi.cube.pubsub.IPublishID;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * 发布消息
 * <p/>
 * 单线程访问
 * <p/>
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IPublish.class)
public class BIPublish implements IPublish {
    private IMessageGenerator messageGenerator;
    private IRouter router;
    private IPublishID publishID;

    public BIPublish(IPublishID publishID) {
        this.publishID = publishID;
        router = BIFactoryHelper.getObject(IRouter.class);
        messageGenerator = BIFactoryHelper.getObject(IMessageGenerator.class);
    }

    @Override
    public void publicMessage(IMessageBody body) throws BIDeliverFailureException {
        try {
            IMessage message = messageGenerator.generateMessage(body);
            router.deliverMessage(message);

        } catch (BIMessageGenerateException e) {
            throw new BIDeliverFailureException(e.getMessage(), e);
        }
    }

    @Override
    public void setTopicTag(ITopicTag topicTag) {
        checkTopicTagRegister(topicTag);
        messageGenerator.changeMessageTopicTag(new BIMessageTopic(topicTag));
    }

    private void checkTopicTagRegister(ITopicTag topicTag) {
        if (!router.isRegistered(topicTag)) {
            try {
                router.registerTopic(topicTag);
            } catch (BITopicDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }

        }
    }

    @Override
    public void setFragmentTag(IFragmentTag fragmentTag) {
        checkFragmentRegister(fragmentTag);
        messageGenerator.changeMessageFragmentTag(new BIMessageFragment(fragmentTag));
    }

    private void checkFragmentRegister(IFragmentTag fragmentTag) {
        if (!router.isRegistered(fragmentTag)) {
            try {
                router.registerFragment(fragmentTag.getSuperTopicTag(), fragmentTag);
            } catch (BIFragmentDuplicateException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            } catch (BITopicAbsentException e) {
                checkTopicTagRegister(fragmentTag.getSuperTopicTag());
                checkFragmentRegister(fragmentTag);
            }

        }
    }

    @Override
    public void setStatusTag(IStatusTag tag) {
        messageGenerator.changeMessageStatusTag(new BIMessageStatus(tag));
    }

    @Override
    public void publicFinishMessage(IMessageBody body) throws BIDeliverFailureException {
        setStatusTag(BIStatusTag.getFinishStatusTag(messageGenerator.getMessageFragmentTag().getFragmentTag()));
        publicMessage(body);
    }

    @Override
    public void publicStopMessage(IMessageBody body) throws BIDeliverFailureException {
        setStatusTag(BIStatusTag.getStopStatusTag(messageGenerator.getMessageFragmentTag().getFragmentTag()));
        publicMessage(body);

    }

    @Override
    public void publicWaitingMessage(IMessageBody body) throws BIDeliverFailureException {
        setStatusTag(BIStatusTag.getWaitingStatusTag(messageGenerator.getMessageFragmentTag().getFragmentTag()));
        publicMessage(body);

    }

    @Override
    public void publicRunningMessage(IMessageBody body) throws BIDeliverFailureException {
        setStatusTag(BIStatusTag.getRunningStatusTag(messageGenerator.getMessageFragmentTag().getFragmentTag()));
        publicMessage(body);

    }

    @Override
    public IMessageFragment getFragmentTag() {
        return messageGenerator.getMessageFragmentTag();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIPublish{");
        sb.append("messageGenerator=").append(messageGenerator);
        sb.append('}');
        return sb.toString();
    }
}
