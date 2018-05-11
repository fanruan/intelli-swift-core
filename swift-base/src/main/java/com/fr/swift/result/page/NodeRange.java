package com.fr.swift.result.page;

/**
 * Created by Lyon on 2018/5/10.
 */
public class NodeRange {

    public static final int UNDEFINED = -1;

    private int startIndexIncluded = UNDEFINED;
    private int endIndexIncluded = UNDEFINED;

    public int getStartIndexIncluded() {
        return startIndexIncluded;
    }

    public int getEndIndexIncluded() {
        return endIndexIncluded;
    }

    public void setStartIndexIncluded(int startIndexIncluded) {
        this.startIndexIncluded = startIndexIncluded;
    }

    public void setEndIndexIncluded(int endIndexIncluded) {
        this.endIndexIncluded = endIndexIncluded;
    }
}
