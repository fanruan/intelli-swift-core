package com.finebi.cube.impl.router.status;

import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IWaitingStatusTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFinishStatusTag extends BIStatusTag {
    public BIFinishStatusTag(IFragmentTag fragmentTag) {
        super(new BIStatusID(IWaitingStatusTag.STATUS_FINISH_TAG), fragmentTag);
    }
}
