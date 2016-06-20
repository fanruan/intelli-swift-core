package com.finebi.cube.impl.router.status;

import com.finebi.cube.router.status.IStatusID;
import com.fr.bi.base.BIIdentity;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusID extends BIIdentity<String> implements IStatusID {
    public BIStatusID(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return identity;

    }
}
