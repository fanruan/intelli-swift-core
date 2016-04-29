package com.finebi.cube.router.status;

import com.finebi.cube.router.IMessageDeliver;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IStatus extends IStatusService, IMessageDeliver {
    IStatusTag getStatusTag();

}
