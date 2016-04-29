package com.finebi.cube.gen.mes;

import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusUtils {
    public static IStatusTag generateStatusFinish(ITopicTag targetTopic, String fragmentID) {
        return BIStatusTag.getFinishStatusTag(BIFragmentUtils.generateFragment(targetTopic, fragmentID));
    }
}
