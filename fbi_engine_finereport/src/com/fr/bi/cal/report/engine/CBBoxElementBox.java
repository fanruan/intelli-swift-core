package com.fr.bi.cal.report.engine;

import com.fr.report.core.box.BoxElement;
import com.fr.report.core.box.BoxElementBox;


public class CBBoxElementBox extends BoxElementBox {

    /**
     *
     */
    private static final long serialVersionUID = -6584701029639221185L;
    private CBBoxElement box;

    public CBBoxElementBox(CBBoxElement box) {
        this.setBox(box);
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public int getRowIndex() {
        return 0;
    }

    @Override
    public BoxElement[] getSonBoxElement() {
        return null;
    }

    @Override
    protected int getExpandDirection() {
        return 0;
    }

    public CBBoxElement getBox() {
        return box;
    }

    public void setBox(CBBoxElement box) {
        this.box = box;
    }

    public int getType() {
        return box.getType();
    }

    @Override
    public int getSortType() {
        return box.getSortType();
    }

}