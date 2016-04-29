package com.finebi.cube.impl.pubsub;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusTagThreshold extends BIBasicThreshold<IStatusTag> {
    @Override
    public IStatusTag getTargetTag(IMessage message) {
        return message.getStatus().getStatusTag();
    }

    @Override
    protected boolean handleOrNot(IMessage message) {
        return message.isStatusMessage();
    }
}
