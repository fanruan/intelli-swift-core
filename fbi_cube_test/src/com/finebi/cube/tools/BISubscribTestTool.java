package com.finebi.cube.tools;

import com.finebi.cube.impl.pubsub.BISubscribeID;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribTestTool {
    public static BISubscribeTestTool generateSubA() {
        return new BISubscribeTestTool(new BISubscribeID("a"));
    }

    public static BISubscribeTestTool generateSubB() {
        return new BISubscribeTestTool(new BISubscribeID("b"));
    }

    public static BISubscribeTestTool generateSubC() {
        return new BISubscribeTestTool(new BISubscribeID("c"));
    }
}
