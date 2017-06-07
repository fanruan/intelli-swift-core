package com.fr.bi.cal.generate.task;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public enum CubeGenerateTaskType {
    ALL(0), PART(1), SINGLE(2), EMPTY(3), CUSTOM(4);

    private int type;

    CubeGenerateTaskType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "";
    }

    public static CubeGenerateTaskType parse(int type) {
        for (CubeGenerateTaskType taskType : CubeGenerateTaskType.values()) {
            if (taskType.type == type) {
                return taskType;
            }
        }
        return CUSTOM;
    }
}
