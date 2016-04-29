package com.finebi.cube.impl;

import com.finebi.cube.pubsub.ISubscribe;
import com.fr.bi.common.container.BISetContainer;
import com.finebi.cube.message.IMessageTopic;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicSubscribeContainer extends BISetContainer<ISubscribe> {
    private IMessageTopic currentTopic;
    private BIFragmentSubscribeContainer fragmentSubscribeContainer;
}
