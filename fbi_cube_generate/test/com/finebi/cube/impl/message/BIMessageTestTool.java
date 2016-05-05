package com.finebi.cube.impl.message;

import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.impl.router.status.BIStatusTestTool;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageFragment;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageTestTool {
    public static IMessage generateMessageTa() {
        IMessage message = buildTopic(BIMessageTopicTestTool.generateTa());
        return message;
    }

    public static IMessage generateMessageStatusFinish() {
        IStatusTag tag = BIStatusTestTool.generateFinishA();
        IMessage message = new BIMessage(new BIMessageTopic(tag.getSuperFragmentTag().getSuperTopicTag()),
                new BIMessageFragment(tag.getSuperFragmentTag()),
                new BIMessageStatus(tag), null);
        return message;
    }

    public static IMessage generateMessageDataSourceStart() {
        IMessage message = buildTopic(new BIMessageTopic(BICubeBuildTopicTag.START_BUILD_CUBE));
        return message;
    }

    private static IMessage buildTopic(IMessageTopic topic) {
        return new BIMessage(topic, null, null, null);
    }

    private static IMessage buildTopicFragment(IMessageTopic topic, IMessageFragment fragmentTag) {
        return new BIMessage(topic, fragmentTag, null, null);
    }

    public static IMessage generateMessageTb() {
        IMessage message = buildTopic(BIMessageTopicTestTool.generateTb());
        return message;
    }

    public static IMessage generateMessageTaFa() {
        IMessage message = buildTopicFragment(BIMessageTopicTestTool.generateTa(), BIMessageFragmentTestTool.generateFa());
        return message;
    }

    public static IMessage generateMessageTbFa() {
        IMessage message = buildTopicFragment(BIMessageTopicTestTool.generateTb(), BIMessageFragmentTestTool.generateFa());
        return message;
    }

    public static IMessage generateMessageTaFaSStop() {
        BIMessage message = new BIMessage(BIMessageTopicTestTool.generateTa(), BIMessageFragmentTestTool.generateFa(),
                new BIMessageStatus(BIStatusTag.getStopStatusTag(BIMessageFragmentTestTool.generateFa().getFragmentTag())), null);
        return message;
    }

}
