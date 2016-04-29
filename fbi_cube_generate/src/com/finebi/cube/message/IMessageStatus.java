package com.finebi.cube.message;

import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/17.
 * Message的Topic和Fragment可以定位到具体Topic实施者。Status表面当前的状态
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessageStatus {

    IStatusTag getStatusTag();
}
