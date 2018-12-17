package com.fr.swift.query.group;

import java.io.Serializable;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelGroupKey implements Serializable {
    private static final long serialVersionUID = -3889379835138230758L;

    private int date;
    private int priceGroup = -1;
    private int tempStrGroup = -1;
    private String strGroup = null;

    public FunnelGroupKey(int date) {
        this.date = date;
    }

    public FunnelGroupKey(int date, int priceGroup) {
        this.date = date;
        this.priceGroup = priceGroup;
    }

    public FunnelGroupKey(int date, int tempStrGroup, String strGroup) {
        this.date = date;
        this.tempStrGroup = tempStrGroup;
        this.strGroup = strGroup;
    }

    public int getTempStrGroup() {
        return tempStrGroup;
    }

    public int getDate() {
        return date;
    }

    public int getPriceGroup() {
        return priceGroup;
    }

    public String getStrGroup() {
        return strGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunnelGroupKey groupKey = (FunnelGroupKey) o;

        if (date != groupKey.date) return false;
        if (priceGroup != groupKey.priceGroup) return false;
        if (tempStrGroup != groupKey.tempStrGroup) return false;
        return strGroup != null ? strGroup.equals(groupKey.strGroup) : groupKey.strGroup == null;
    }

    @Override
    public int hashCode() {
        int result = date;
        result = 31 * result + priceGroup;
        result = 31 * result + tempStrGroup;
        result = 31 * result + (strGroup != null ? strGroup.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "groupKey[date=" + date + ", priceGroup=" + priceGroup + ", tempStrGroup="
                + tempStrGroup + ", strGroup=" + strGroup + "]";
    }
}

