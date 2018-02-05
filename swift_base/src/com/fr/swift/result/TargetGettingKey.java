package com.fr.swift.result;

public class TargetGettingKey {
    private int targetIndex;

    public TargetGettingKey(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public int getTargetIndex() {
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