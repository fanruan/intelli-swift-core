package com.fr.bi.stable.engine.index.key;

import com.fr.bi.base.key.BIKey;
import com.fr.general.ComparatorUtils;

/**
 * 索引列的唯一标识
 * Created by GUY on 2015/4/10.
 */
public class IndexKey implements BIKey {

    private String index;

    public IndexKey(String index) {
        this.index = index;
    }

    @Override
    public String getKey() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IndexKey indexKey = (IndexKey) o;

        return ComparatorUtils.equals(index, indexKey.index);

    }

    @Override
    public int hashCode() {
        return index == null ? 0 : index.hashCode();
    }

    @Override
    public String toString() {
        return "IndexKey{" +
                "index='" + index + '\'' +
                '}';
    }
}