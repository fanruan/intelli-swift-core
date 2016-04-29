package com.fr.bi.stable.structure.object;

import com.fr.stable.FCloneable;


/**
 * Created by 小灰灰 on 13-12-31.
 * 各位任意进制的整数
 */
public class XInteger implements FCloneable {

    private int[] num;

    private int[] limit;

    public XInteger(int[] limit) {
        this.limit = limit;
        this.num = new int[limit.length];
    }

    public void plus() {
        if (num.length == 0) {
            return;
        }
        num[num.length - 1] += 1;
        checklimit();
    }

    public void plus(int index) {
        if (num.length == 0) {
            return;
        }
        num[index] += 1;
        for (int i = index + 1; i < num.length; i++) {
            num[i] = 0;
        }
        checklimit();
    }

    public int[] getIndex() {
        return num;
    }

    public int[] getLimit() {
        return limit;
    }

    private void checklimit() {
        for (int i = num.length - 1; i > 0; i--) {
            if (num[i] == limit[i]) {
                num[i] = 0;
                num[i - 1] += 1;
            }
        }
        if (num[0] == limit[0]) {
            num = null;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        int[] cLimit = new int[limit.length];
        for (int i = 0; i < limit.length; i++) {
            cLimit[i] = limit[i];
        }
        XInteger clone = new XInteger(cLimit);
        int[] cnum = new int[num.length];
        for (int i = 0; i < num.length; i++) {
            cnum[i] = num[i];
        }
        clone.num = cnum;
        return clone;
    }
}