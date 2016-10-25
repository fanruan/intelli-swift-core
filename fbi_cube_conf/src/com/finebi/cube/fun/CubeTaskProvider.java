package com.finebi.cube.fun;

import com.fr.bi.stable.engine.CubeTask;
import com.fr.stable.fun.mark.Mutable;

/**
 * Cube执行更新定时更新的时候不仅要检查时间是否到了，还要检查这个条件
 */
public interface CubeTaskProvider extends Mutable {

    String MARK_STRING = "CubeTaskProvider";

    int CURRENT_LEVEL = 1;

    boolean accept(CubeTask task);

    void addTaskIfConditionMeet(CubeTask task, Action callback);

    interface Action {

        void addTask();
    }
}
