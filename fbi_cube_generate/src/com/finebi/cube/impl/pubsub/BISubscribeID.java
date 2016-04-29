package com.finebi.cube.impl.pubsub;

import com.finebi.cube.pubsub.ISubscribeID;
import com.fr.bi.base.BIIdentity;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BISubscribeID extends BIIdentity<String> implements ISubscribeID {
    public BISubscribeID(String id) {
        super(id);
    }
}
