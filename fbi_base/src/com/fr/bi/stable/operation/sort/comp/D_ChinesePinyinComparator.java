package com.fr.bi.stable.operation.sort.comp;

/**
 * Created by GUY on 2015/2/12.
 */
public class D_ChinesePinyinComparator extends ChinesePinyinComparator {

    /**
     *
     */
    private static final long serialVersionUID = 550953059460281816L;

    @Override
    public int compare(String o1, String o2) {
        String str1 = o2;
        String str2 = o1;
        return super.compare(str1, str2);
    }
}