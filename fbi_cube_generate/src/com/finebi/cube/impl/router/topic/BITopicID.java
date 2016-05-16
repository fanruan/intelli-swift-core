package com.finebi.cube.impl.router.topic;

import com.finebi.cube.router.topic.ITopicID;
import com.fr.bi.base.BIIdentity;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicID extends BIIdentity<String> implements ITopicID {
    public BITopicID(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return identity;

    }
}
