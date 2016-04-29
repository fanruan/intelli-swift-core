package com.finebi.cube.impl.operate;

import com.finebi.cube.operate.IOperationID;
import com.fr.bi.base.BIIdentity;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIOperationID extends BIIdentity<String> implements IOperationID {

    public BIOperationID(String id) {
        super(id);
    }
}
