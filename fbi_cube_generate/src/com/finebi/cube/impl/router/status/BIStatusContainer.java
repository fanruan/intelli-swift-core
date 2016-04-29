package com.finebi.cube.impl.router.status;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.exception.BIStatusAbsentException;
import com.finebi.cube.exception.BIStatusDuplicateException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.status.IStatus;
import com.finebi.cube.router.status.IStatusContainer;
import com.finebi.cube.router.status.IStatusTag;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IStatusContainer.class)
public class BIStatusContainer extends BIMapContainer<IStatusTag, IStatus> implements IStatusContainer {
    @Override
    protected Map<IStatusTag, IStatus> initContainer() {
        return new HashMap<IStatusTag, IStatus>();
    }

    @Override
    protected IStatus generateAbsentValue(IStatusTag key) {
        return null;
    }

    @Override
    public Collection<IStatus> getAllStatus() {
        synchronized (container) {
            return container.values();
        }
    }

    @Override
    public void registerStatus(IStatus status) throws BIStatusDuplicateException {
        BINonValueUtils.checkNull(status);
        BINonValueUtils.checkNull(status.getStatusTag());
        try {
            putKeyValue(status.getStatusTag(), status);
        } catch (BIKeyDuplicateException ignore) {
            throw new BIStatusDuplicateException();
        }
    }

    @Override
    public IStatus getSpecificStatus(IStatusTag statusTag) throws BIStatusAbsentException {
        try {
            return getValue(statusTag);
        } catch (BIKeyAbsentException ignore) {
            throw new BIStatusAbsentException();
        }
    }

    @Override
    public boolean contain(IStatusTag statusTag) {
        return containsKey(statusTag);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message != null && message.getStatus() != null) {
            try {
                IStatus status = getSpecificStatus(message.getStatus().getStatusTag());
                status.deliverMessage(message);
            } catch (BIStatusAbsentException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

    }
}
