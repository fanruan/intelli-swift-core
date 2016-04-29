package com.finebi.cube.impl.router.status;

import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IWaitingStatusTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIWaitingStatusTag extends BIStatusTag {
    public BIWaitingStatusTag(IFragmentTag fragmentTag) {
        super(new BIStatusID(IWaitingStatusTag.STATUS_WAITING_TAG), fragmentTag);
    }
}
