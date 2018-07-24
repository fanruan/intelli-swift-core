package com.fr.swift.query.group.info;

/**
 * Created by Lyon on 2018/7/24.
 */
public class IndexInfoImpl implements IndexInfo {

    private boolean isIndexed;
    private boolean isGlobalIndexed;

    public IndexInfoImpl(boolean isIndexed, boolean isGlobalIndexed) {
        this.isIndexed = isIndexed;
        this.isGlobalIndexed = isGlobalIndexed;
    }

    @Override
    public boolean isIndexed() {
        return isIndexed;
    }

    @Override
    public boolean isGlobalIndexed() {
        return isGlobalIndexed;
    }
}
