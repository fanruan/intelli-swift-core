package com.fr.swift.segment.column;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/10/9
 */
public class ColumnKey implements Serializable {
    private static final long serialVersionUID = -8348275900712099698L;
    private String name;

    public ColumnKey(String name) {
        this.name = name;
    }

    public ColumnKey() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ColumnKey columnKey = (ColumnKey) o;

        return name != null ? name.equals(columnKey.name) : columnKey.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
