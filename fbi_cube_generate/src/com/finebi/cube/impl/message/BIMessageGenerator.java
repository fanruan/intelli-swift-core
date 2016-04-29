package com.finebi.cube.impl.message;

import com.finebi.cube.exception.BIMessageGenerateException;
import com.finebi.cube.message.*;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;

/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IMessageGenerator.class)
public class BIMessageGenerator implements IMessageGenerator {
    private IMessageTopic topicTag;
    private IMessageFragment fragmentTag;
    private IMessageStatus statusTag;

    @Override
    public IMessage generateMessage(IMessageBody body) throws BIMessageGenerateException {
        checkMessageComponent();
        IMessage message = new BIMessage(topicTag, fragmentTag, statusTag, body);
        return message;
    }

    void checkMessageComponent() throws BIMessageGenerateException {
        if (fragmentTag != null && topicTag == null) {
            throw new BIMessageGenerateException();
        } else if ((fragmentTag == null || topicTag == null) && statusTag != null) {
            throw new BIMessageGenerateException();

        }
    }

    @Override
    public void changeMessageTopicTag(IMessageTopic topicTag) {
        this.topicTag = topicTag;
    }

    @Override
    public void changeMessageFragmentTag(IMessageFragment fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    @Override
    public void changeMessageStatusTag(IMessageStatus statusTag) {
        this.statusTag = statusTag;
    }

    @Override
    public IMessageTopic getMessageTopicTag() {
        return this.topicTag;
    }

    @Override
    public IMessageFragment getMessageFragmentTag() {
        return this.fragmentTag;
    }

    @Override
    public IMessageStatus getMessageStatusTag() {
        return this.statusTag;
    }
}
