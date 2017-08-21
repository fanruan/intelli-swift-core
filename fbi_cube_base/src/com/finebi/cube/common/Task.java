package com.finebi.cube.common;

/**
 * This class created on 2017/7/3.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

public interface Task {
    boolean isFree();

    void execute();

    String currentStage();

    void registerStage(Stage stage);
}
