package com.finebi.cube.common;

/**
 * This class created on 2017/7/3.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface Stage {
    boolean execute(TaskContext taskContext);

    String stageTag();
}
