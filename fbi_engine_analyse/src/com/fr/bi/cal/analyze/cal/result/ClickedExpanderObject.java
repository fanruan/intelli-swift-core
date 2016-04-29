package com.fr.bi.cal.analyze.cal.result;

import com.fr.general.ComparatorUtils;

import java.util.ArrayList;

/**
 * Created by 小灰灰 on 2014/12/18.
 */
public class ClickedExpanderObject {
    private ArrayList clickedObject;
    private boolean isTop;

    public ClickedExpanderObject(ArrayList clickedObject, boolean isTop) {
        this.clickedObject = clickedObject;
        this.isTop = isTop;
    }

    public ArrayList getClickedObject(boolean isTop) {
        if (ComparatorUtils.equals(this.isTop, isTop)) {
            return clickedObject;
        }
        return null;
    }
}