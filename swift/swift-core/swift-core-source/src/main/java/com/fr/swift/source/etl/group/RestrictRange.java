package com.fr.swift.source.etl.group;

/**
 * Created by Handsome on 2018/2/22 0022 17:53
 */
public class RestrictRange {
    private boolean closemax;
    private boolean closemin;
    private String groupName;
    private String column;
    private double max;
    private double min;
    private boolean valid;

    public RestrictRange(boolean closemax, boolean closemin, String groupName,
                         String column, double max, double min, boolean valid) {
        this.closemax = closemax;
        this.closemin = closemin;
        this.groupName = groupName;
        this.column = column;
        this.max = max;
        this.min = min;
        this.valid = valid;
    }

    public boolean isClosemax() {
        return closemax;
    }

    public boolean isClosemin() {
        return closemin;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getColumn() {
        return column;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean match(double value) {
        boolean match = false;
        match = (closemax ? (value <= max) : (value < max)) &&
                (closemin ? (min <= value) : (min < value));
        return match;
    }
}
