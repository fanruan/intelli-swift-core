package com.finebi.cube.impl.pubsub;

import com.finebi.cube.impl.router.BISubscribe4Test;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribTestTool {
    public static BISubscribe4Test generateSubA() {
        return new BISubscribe4Test(new BISubscribeID("a"));
    }

    public static BISubscribe4Test generateSubB() {
        return new BISubscribe4Test(new BISubscribeID("b"));
    }

    public static BISubscribe4Test generateSubC() {
        return new BISubscribe4Test(new BISubscribeID("c"));
    }
}
