package com.fr.swift.cloud.query.group;

import com.fr.swift.cloud.compare.Comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelGroupKey implements Serializable, Comparable<FunnelGroupKey> {
    private static final long serialVersionUID = -3889379835138230758L;
    private Comparator comparator = Comparators.STRING_ASC;

    private String date;
    private int priceGroup = -1;
    private int tempStrGroup = -1;
    private String strGroup = null;
    private List<Double> rangePair = null;
    private GroupType type;

    public FunnelGroupKey(String date) {
        this.date = date;
        this.type = GroupType.NONE;
    }

    public FunnelGroupKey(String date, int priceGroup, List<Double> rangePair) {
        this.date = date;
        this.priceGroup = priceGroup;
        this.type = GroupType.RANGE;
        this.rangePair = rangePair;
    }

    public FunnelGroupKey(String date, int tempStrGroup, String strGroup) {
        this.date = date;
        this.tempStrGroup = tempStrGroup;
        this.strGroup = strGroup;
        this.type = GroupType.NORMAL;
    }

    public GroupType getType() {
        return type;
    }

    public List<Double> getRangePair() {
        return rangePair;
    }

    public int getTempStrGroup() {
        return tempStrGroup;
    }

    public String getDate() {
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

        FunnelGroupKey key = (FunnelGroupKey) o;

        if (priceGroup != key.priceGroup) return false;
        if (tempStrGroup != key.tempStrGroup) return false;
        if (date != null ? !date.equals(key.date) : key.date != null) return false;
        return strGroup != null ? strGroup.equals(key.strGroup) : key.strGroup == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
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

    @Override
    public int compareTo(FunnelGroupKey o) {
        int compare = comparator.compare(date, o.date);
        if (compare == 0) {
            switch (type) {
                case RANGE:
                    String current = rangePair.get(0) + "-" + rangePair.get(1);
                    String other = o.rangePair.get(0) + "-" + o.rangePair.get(1);
                    return comparator.compare(current, other);
                case NORMAL:
                    return comparator.compare(strGroup, o.strGroup);
                default:
            }
        }
        return compare;
    }

    public enum GroupType {
        NONE,
        NORMAL,
        RANGE
    }
}

