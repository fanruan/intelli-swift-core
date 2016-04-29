package com.finebi.cube.router.fragment;

import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IFragmentTag {

    IFragmentID getFragmentID();

    ITopicTag getSuperTopicTag();

    boolean isValid();
}
