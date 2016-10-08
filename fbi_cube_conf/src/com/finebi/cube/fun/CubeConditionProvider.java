package com.finebi.cube.fun;

import com.fr.stable.fun.mark.Mutable;

/**
 * Cube执行更新定时更新的时候不仅要检查时间是否到了，还要检查这个条件
 */
public interface CubeConditionProvider extends Mutable {

    String MARK_STRING = "CubeConditionProvider";

    int CURRENT_LEVEL = 1;

    void prepare();

    void end();
}
