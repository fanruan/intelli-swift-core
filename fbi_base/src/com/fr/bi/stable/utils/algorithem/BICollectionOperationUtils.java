package com.fr.bi.stable.utils.algorithem;

import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2015/12/4.
 */
public class BICollectionOperationUtils {

    /**
     * 合并a和b ，去掉重复值
     *
     * @param a 关键字
     * @param b 关键字
     * @return 关键字
     */
    public static IntList union(IntList a, IntList b) {
        try {
            if (a == b && a != null) {
                return (IntList) a.clone();
            } else if (a == null || a.size() == 0) {
                if (b == null) {
                    return null;
                }
                return (IntList) b.clone();
            } else if (b == null || b.size() == 0) {
                return (IntList) a.clone();
            }
        } catch (CloneNotSupportedException e) {
        }
        IntList res = null;
        try {
            res = (IntList) a.clone();
        } catch (CloneNotSupportedException e) {
        }

        for (int i = 0, len = b.size(); i < len; i++) {
            int b_value = b.get(i);
            if (!res.contains(b_value)) {
                res.add(b_value);
            }
        }
        return res;
    }
    public static boolean listEqual(List left, List right) {
        if (left.size() != right.size()) {
            return false;
        }
        Iterator itL = left.iterator();
        Iterator itR = right.iterator();
        while (itL.hasNext() && itR.hasNext()) {
            if (!ComparatorUtils.equals(itL.next(), itR.next())) {
                return false;
            }
        }
        return !itL.hasNext() && !itR.hasNext();
    }
}