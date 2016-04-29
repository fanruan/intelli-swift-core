package com.finebi.cube.impl.pubsub;

import com.finebi.cube.pubsub.IPublishID;
import com.fr.bi.base.BIIdentity;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIPublishID extends BIIdentity<String> implements IPublishID {
    public BIPublishID(String id) {
        super(id);
    }
}