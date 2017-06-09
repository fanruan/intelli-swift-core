package com.fr.bi.report.key;

public class TargetGettingKey {

    private String targetName;
    //pony index决定了是哪个target，node汇总直接用数组保存，
    private int targetIndex;

    public TargetGettingKey(int targetIndex, String targetName) {
        this.targetIndex = targetIndex;
        this.targetName = targetName;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getTargetIndex(){
        return targetIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TargetGettingKey that = (TargetGettingKey) o;

        return targetIndex == that.targetIndex;
    }

    @Override
    public int hashCode() {
        return targetIndex;
    }
}