package com.finebi.cube.conf.singletable;


import com.fr.bi.common.inter.ValueCreator;

import java.util.TimerTask;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BICubeTimeTaskCreator extends ValueCreator<TimerTask> {
    String XML_TAG = "BICubeTimeTaskCreator";

    void taskCreate(long userId);
}