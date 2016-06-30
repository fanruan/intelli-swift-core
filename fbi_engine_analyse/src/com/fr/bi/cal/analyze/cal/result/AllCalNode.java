package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.report.result.DimensionCalculator;

/**
 * Created by loy on 16/6/29.
 */
public class AllCalNode extends Node {

    public AllCalNode(DimensionCalculator key, Object data) {
        super(key, data);
    }

    public int getIndexByValue(Object value, boolean sorted) {
        if(sorted) {
            return getMinCompareValue(0, getChildLength() - 1, value);
        }
        else {
            for (int i = 0; i < childs.size(); i++) {
                Object c = childs.get(i);
                if(c.equals(value)){
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 找出等于前值或者正好大一点的那个值
     *
     * @param start
     * @param end
     * @param value
     * @return
     */
    private int getMinCompareValue(int start, int end, Object value) {
        if (start > end) {
            return start;
        }
        int index = (start + end) / 2;
        AllCalNode c = (AllCalNode) childs.get(index);
        int result = getComparator().compare(value, c.getData());
        if (result > 0) {
            return getMinCompareValue(index + 1, end, value);
        } else if (result < 0) {
            return getMinCompareValue(start, index - 1, value);
        } else {
            return index;
        }
    }
}
