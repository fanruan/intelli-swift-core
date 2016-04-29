package com.fr.bi.stable.engine;

public enum CubeTaskType {

    CHECK(0), ALL(1), SINGLE(2), BUILD(3);

    private int type;

    CubeTaskType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + "";
    }

    public static CubeTaskType parse(int type) {
        for (CubeTaskType taskType : CubeTaskType.values()) {
            if (taskType.type == type) {
                return taskType;
            }
        }
        return CHECK;
    }


}