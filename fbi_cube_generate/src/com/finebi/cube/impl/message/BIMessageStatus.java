package com.finebi.cube.impl.message;

import com.finebi.cube.message.IMessageStatus;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageStatus implements IMessageStatus {
    private IStatusTag statusTag;

    public BIMessageStatus(IStatusTag statusTag) {
        this.statusTag = statusTag;
    }

    @Override
    public IStatusTag getStatusTag() {
        return statusTag;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BIMessageStatus{");
        sb.append("statusTag=").append(statusTag);
        sb.append('}');
        return sb.toString();
    }
}
