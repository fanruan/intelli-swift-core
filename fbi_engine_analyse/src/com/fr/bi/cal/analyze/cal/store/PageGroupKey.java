package com.fr.bi.cal.analyze.cal.store;


import com.fr.general.ComparatorUtils;

import java.util.HashSet;

/**
 * Created by 小灰灰 on 14-3-7.
 */
public class PageGroupKey {

    private HashSet<GroupKey> keys;

    public PageGroupKey(HashSet<GroupKey> keys) {
        this.keys = keys;
    }

    public HashSet<GroupKey> getKeys() {
        return keys;
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) {
            return false;
        }
        if (!(ob instanceof PageGroupKey)) {
            return false;
        }

        return ComparatorUtils.equals(keys, ((PageGroupKey) ob).keys);
    }
}