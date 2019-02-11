package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 * @date 2017/11/7
 */
abstract class BaseDetailColumn<T, W extends Writer, R extends Reader> implements DetailColumn<T> {
    private static final String DETAIL = "detail";

    static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    IResourceLocation location;

    W detailWriter;
    R detailReader;

    BaseDetailColumn(IResourceLocation parent) {
        location = parent.buildChildLocation(DETAIL);
    }

    @Override
    public int getInt(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public long getLong(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public double getDouble(int pos) {
        return Crasher.crash("not allowed");
    }

    protected abstract void initDetailReader();

    @Override
    public boolean isReadable() {
        initDetailReader();
        boolean readable = detailReader.isReadable();
        if (location.getStoreType().isPersistent()) {
            IoUtil.release(detailReader);
        }
        detailReader = null;
        return readable;
    }

    @Override
    public void flush() {
        if (detailWriter != null) {
            detailWriter.flush();
        }
    }

    @Override
    public void release() {
        IoUtil.release(detailWriter, detailReader);
        detailWriter = null;
        detailReader = null;
    }
}