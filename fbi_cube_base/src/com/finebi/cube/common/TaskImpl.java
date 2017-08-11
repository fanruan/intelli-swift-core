package com.finebi.cube.common;
/**
 * This class created on 2017/7/3.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskImpl implements Task {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(TaskImpl.class);
    private List<Stage> stages = new ArrayList<Stage>();
    private boolean isFree = true;
    private TaskContext context = new TaskContext();
    private static Stage EMPTY_STAGE = new Stage() {
        @Override
        public boolean execute(TaskContext taskContext) {
            return false;
        }

        @Override
        public String stageTag() {
            return "waiting";
        }
    };
    private Stage currentStage = EMPTY_STAGE;

    @Override
    public boolean isFree() {
        return isFree;
    }

    @Override
    public void execute() {
        try {
            if (isFree) {
                isFree = false;
                for (Stage stage : stages) {
                    currentStage = stage;
                    try {
                        if (!stage.execute(context)) {
                            break;
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        break;
                    }
                }
            }
        } finally {
            isFree = true;
            currentStage = EMPTY_STAGE;
        }
    }

    @Override
    public String currentStage() {
        return currentStage.stageTag();
    }

    @Override
    public void registerStage(Stage stage) {
        stages.add(stage);
    }
}
