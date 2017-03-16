package com.fr.bi.common.persistent.writer;

/**
 * Created by Young's on 2016/8/3.
 */
public class NormalString4SpecialChar {
    public String value;

    public String getValue() {
        return value;
    }

    public void setValue(String sql) {
        this.value = sql;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NormalString4SpecialChar test = (NormalString4SpecialChar) o;

        return value != null ? value.equals(test.value) : test.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
