package com.fr.bi.cal.report.engine;

import com.fr.bi.stable.constant.CellConstant;
import com.fr.report.core.box.BoxElementBox;
import com.fr.report.core.box.BoxElementFull;

import java.util.Iterator;
import java.util.List;

public class CBBoxElement extends BoxElementFull {
    /**
     *
     */
    private static final long serialVersionUID = 5191587389333260864L;
    private List cells;
    private String name;
    private int type;
    private int sortType = CellConstant.CBCELL.SORT.UNSORTFIELD;
    private String dimensionJSON;
    private String indexString_x;
    private String indexString_y;
    private boolean isExpand;
    private String sortTargetName;
    private String sortTargetValue;
    private int dimensionRegionIndex = 0;

    public CBBoxElement(List cells) {
        this.cells = cells;
        initCellBE();
    }

    public String getSortTargetName() {
        return sortTargetName;
    }

    public void setSortTargetName(String sortTargetName) {
        this.sortTargetName = sortTargetName;
    }

    public String getSortTargetValue() {
        return sortTargetValue;
    }

    public void setSortTargetValue(String sortTargetValue) {
        this.sortTargetValue = sortTargetValue;
    }

    public String getDimensionJSON() {
        return this.dimensionJSON;
    }

    public void setDimensionJSON(String dimensionJSON) {
        this.dimensionJSON = dimensionJSON;
    }

    public String getIndexString_x() {
        return indexString_x;
    }

    public void setIndexString_x(String indexString) {
        this.indexString_x = indexString;
    }

    public String getDimensionRegionIndex() {
        return new Integer(dimensionRegionIndex).toString();
    }

    public void setDimensionRegionIndex(int regionIndex) {
        dimensionRegionIndex = regionIndex;
    }

    /**
     * @return
     */
    public String getIndexString_y() {
        return indexString_y;
    }

    public void setIndexString_y(String indexString) {
        this.indexString_y = indexString;
    }

    /**
     * 是否展开
     *
     * @return 是否展开
     */
    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    private void initCellBE() {
        for (int i = 0, len = getResultBoxSize(); i < len; i++) {
            ((CBCell) cells.get(i)).setBoxElement(this);
        }
    }

    @Override
    public int getBEIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BoxElementBox getBEB() {
        return new CBBoxElementBox(this);
    }

    @Override
    public Iterator getResultBoxIterator() {
        return cells == null ? null : cells.iterator();
    }

    @Override
    public int getResultBoxSize() {
        return cells == null ? 0 : cells.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    /**
     * 是否有图标
     *
     * @return 是否
     */
    public boolean hasIcon() {
        if (getType() == CellConstant.CBCELL.DIMENSIONTITLE_Y || getType() == CellConstant.CBCELL.DIMENSIONTITLE_X
                || getIndexString_x() != null || getIndexString_y() != null) {
            return true;
        }

        return false;
    }
}