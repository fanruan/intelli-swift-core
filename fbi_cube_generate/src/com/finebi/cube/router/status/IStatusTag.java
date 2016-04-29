package com.finebi.cube.router.status;

import com.finebi.cube.router.fragment.IFragmentTag;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IStatusTag {
    String STATUS_WAITING_TAG = "waiting_status";
    String STATUS_STOP_TAG = "stop_status";
    String STATUS_RUNNING_TAG = "running_status";
    String STATUS_FINISH_TAG = "finish_status";

    IStatusID getStatusID();

    IFragmentTag getSuperFragmentTag();

    boolean isValid();

}
