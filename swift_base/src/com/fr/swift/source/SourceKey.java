package com.fr.swift.source;

import com.fr.swift.util.Util;

/**
 * Created by pony on 2017/10/24.
 */
public class SourceKey {
    private String id;

    public SourceKey(String id) {
        Util.requireNonNull(id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceKey sourceKey = (SourceKey) o;

        return id != null ? id.equals(sourceKey.id) : sourceKey.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
