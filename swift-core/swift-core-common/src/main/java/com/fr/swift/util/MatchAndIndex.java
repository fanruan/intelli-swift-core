package com.fr.swift.util;

/**
 * Created by 小灰灰 on 2017/6/27.
 */
public class MatchAndIndex {
    private boolean match;
    private int index;

    public MatchAndIndex(boolean match, int index) {
        this.match = match;
        this.index = index;
    }

    public boolean isMatch() {
        return match;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MatchAndIndex that = (MatchAndIndex) o;

        if (match != that.match) {
            return false;
        }
        return index == that.index;
    }

    @Override
    public int hashCode() {
        int result = (match ? 1 : 0);
        result = 31 * result + index;
        return result;
    }
}
