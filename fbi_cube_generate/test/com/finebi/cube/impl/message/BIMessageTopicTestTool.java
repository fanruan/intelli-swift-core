package com.finebi.cube.impl.message;

import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.message.IMessageTopic;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageTopicTestTool {
    public static IMessageTopic generateTa() {
        return new BIMessageTopic(BITopicTagTestTool.getTopicTagA());
    }

    public static IMessageTopic generateTb() {
        return new BIMessageTopic(BITopicTagTestTool.getTopicTagB());
    }

    public static IMessageTopic generateTc() {
        return new BIMessageTopic(BITopicTagTestTool.getTopicTagC());
    }
}
