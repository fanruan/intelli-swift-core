package com.finebi.cube.impl.router.topic;

import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicTagTestTool {
    public static ITopicTag getTopicTagA() {
        return new BITopicTag(new BITopicID("A"));
    }

    public static ITopicTag getTopicTagB() {
        return new BITopicTag(new BITopicID("B"));
    }

    public static ITopicTag getTopicTagC() {
        return new BITopicTag(new BITopicID("C"));
    }

    public static ITopicTag getTopicTagFirst() {
        return new BITopicTag(new BITopicID("First"));
    }

    public static ITopicTag getTopicTagSecond() {
        return new BITopicTag(new BITopicID("Second"));
    }

    public static ITopicTag getTopicTagThrid() {
        return new BITopicTag(new BITopicID("Third"));
    }

    public static ITopicTag getTopicTagStart() {
        return new BITopicTag(new BITopicID("Start"));
    }

}
