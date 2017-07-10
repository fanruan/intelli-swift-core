package com.fr.bi.stable.io.sortlist;

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

    public boolean isMatch(){
        return match;
    }

    public int getIndex(){
        return index;
    }
}
