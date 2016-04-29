package com.finebi.cube.impl;

import com.finebi.cube.message.IMessageFragment;

import java.util.Map;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicSubscribeManager {
    private BITopicSubscribeContainer subscribes;
    private Map<IMessageFragment, BIFragmentSubscribeContainer> fragmentSubscribeManagerMap;

}
