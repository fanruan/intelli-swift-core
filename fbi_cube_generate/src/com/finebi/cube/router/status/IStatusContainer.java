package com.finebi.cube.router.status;

import com.finebi.cube.exception.BIStatusAbsentException;
import com.finebi.cube.exception.BIStatusDuplicateException;
import com.finebi.cube.router.IMessageDeliver;

import java.util.Collection;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IStatusContainer extends IMessageDeliver {
    Collection<IStatus> getAllStatus();

    void registerStatus(IStatus status) throws BIStatusDuplicateException;

    IStatus getSpecificStatus(IStatusTag statusTag) throws BIStatusAbsentException;

    boolean contain(IStatusTag statusTag);
}
