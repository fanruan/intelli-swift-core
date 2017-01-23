package com.fr.bi.etl.analysis.monitor;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

/**
 * Created by daniel on 2017/1/22.
 */
public class SimpleTable {
    private String id;

    SimpleTable(String id) {
        this.id = id;
    }

    public String toString(){
        return id == null ? StringUtils.EMPTY : id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTable biTable = (SimpleTable) o;

        return  ComparatorUtils.equals(id, biTable.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }
}
