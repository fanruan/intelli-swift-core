package com.fr.bi.field;


import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.general.ComparatorUtils;

/**
 * Created by GUY on 2015/4/8.
 */
public class BITargetAndDimensionUtils {

    /**
     * 这种恶心巴拉的东西
     *
     * @param colDimension 维度
     * @return true或fasle
     */
    public static boolean isTargetSort(BIDimension[] colDimension) {
        int len = colDimension.length;
        if (len == 0) {
            return false;
        }
        BIDimension t = colDimension[0];
        if (t.getSortTarget() == null) {
            return false;
        }
        for (int i = 1; i < colDimension.length; i++) {
            boolean b = ComparatorUtils.equals(t.getSortTarget(), colDimension[i].getSortTarget())
                    && t.getSortType() == colDimension[i].getSortType();
            if (!b) {
                return false;
            }
        }
        return true;
    }

}