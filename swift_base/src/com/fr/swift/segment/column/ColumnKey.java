package com.fr.swift.segment.column;

import com.fr.swift.source.RelationSource;

/**
 * @author pony
 * @date 2017/10/9
 */
public class ColumnKey {
    private final String name;

    private RelationSource relation;

    public ColumnKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RelationSource getRelation() {
        return relation;
    }

    public void setRelation(RelationSource relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnKey columnKey = (ColumnKey) o;

        return name != null ? name.equals(columnKey.name) : columnKey.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
