package com.fr.bi.stable.operation.sort.comp;

import com.fr.general.ComparatorUtils;

import java.io.Serializable;

/**
 * Created by GUY on 2015/2/12.
 */
public class CustomComparator extends AbstractComparator<String> implements Serializable {

    private static final long serialVersionUID = 5509530594602823126L;

    private String[] sortReg;

    public String[] getSortReg() {
        return sortReg;
    }

    public void setSortReg(String[] sortReg) {
        this.sortReg = sortReg;
    }

    @Override
    public int compare(String o1, String o2) {
        if (sortReg == null) {
            return ComparatorUtils.compare(o1, o2);
        } else {
            return ComparatorUtils.compare(getIndex(o1), getIndex(o2));
        }
    }

    //如果不符合规则的全部靠后
    protected int getIndex(String o) {
        int len = sortReg.length;
        for (int i = 0; i < len; i++) {
            if (ComparatorUtils.equals(o == null ? o : o.toString(), sortReg[i].toString())) {
                return i;
            }
        }
        return len + Math.abs(o.hashCode());
    }
}