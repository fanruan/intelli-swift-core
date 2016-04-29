package com.finebi.cube.impl.pubsub;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.fragment.IFragmentTag;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentTagThreshold extends BIBasicThreshold<IFragmentTag> {
    @Override
    public IFragmentTag getTargetTag(IMessage message) {
        return message.getFragment().getFragmentTag();
    }

    @Override
    protected boolean handleOrNot(IMessage message) {
        return message.isFragmentMessage();
    }
}
