package com.fr.bi.cal.analyze.cal.store;

/**
 * Created by sheldon on 14-9-10.
 */
public class ComplexRegionPageInfor {
    private int startPage = 0;
    private int endPage = 0;
    private int shiftCount = 0;
    private int regionIndex = 0;

    public ComplexRegionPageInfor() {

    }

    public int getRegionIndex() {
        return regionIndex;
    }

    public void setRegionIndex(int index) {
        regionIndex = index;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int page) {
        startPage = page;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int page) {
        endPage = page;
    }

    public int getShiftCount() {
        return shiftCount;
    }

    public void setShiftCount(int count) {
        shiftCount = count;
    }
}