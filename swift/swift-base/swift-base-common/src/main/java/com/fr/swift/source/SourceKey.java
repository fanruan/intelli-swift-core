package com.fr.swift.source;

import com.fr.swift.util.Assert;

import java.io.Serializable;

/**
 * @author pony
 * @date 2017/10/24
 */
public class SourceKey implements Serializable {

    private static final long serialVersionUID = 6550450020284609023L;
    private String id;

    public SourceKey(String id) {
        Assert.notNull(id);
        this.id = id;
    }

    public SourceKey() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SourceKey sourceKey = (SourceKey) o;

        return id != null ? id.equals(sourceKey.id) : sourceKey.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return id;
    }
}
