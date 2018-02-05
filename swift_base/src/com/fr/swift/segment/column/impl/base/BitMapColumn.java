package com.fr.swift.segment.column.impl.base;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.segment.column.BitmapIndexedColumn;

/**
 * @author anchore
 * @date 2017/11/9
 */
public class BitMapColumn implements BitmapIndexedColumn {
    private static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    private BitMapWriter writer;
    private BitMapReader reader;
    private IResourceLocation indexLocation;

    private BitMapWriter nullWriter;
    private BitMapReader nullReader;
    private IResourceLocation nullLocation;

    public BitMapColumn(IResourceLocation parent) {
        this.indexLocation = parent.buildChildLocation(INDEX);
        this.nullLocation = parent.buildChildLocation(NULL_INDEX);
    }

    private void initIndexWriter() {
        if (writer == null) {
            writer = (BitMapWriter) DISCOVERY.getWriter(indexLocation, new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
    }

    private void initIndexReader() {
        if (reader == null) {
            reader = (BitMapReader) DISCOVERY.getReader(indexLocation, new BuildConf(IoType.READ, DataType.BITMAP));
        }
    }

    private void initNullIndexWriter() {
        if (nullWriter == null) {
            nullWriter = (BitMapWriter) DISCOVERY.getWriter(nullLocation, new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
    }

    private void initNullIndexReader() {
        if (nullReader == null) {
            nullReader = (BitMapReader) DISCOVERY.getReader(nullLocation, new BuildConf(IoType.READ, DataType.BITMAP));
        }
    }

    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        initIndexWriter();
        writer.put(index, bitmap);
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        initIndexReader();
        return reader.get(index);
    }

    @Override
    public void putNullIndex(ImmutableBitMap bitMap) {
        initNullIndexWriter();
        nullWriter.put(0, bitMap);
    }

    @Override
    public ImmutableBitMap getNullIndex() {
        initNullIndexReader();
        return nullReader.get(0);
    }

    @Override
    public void flush() {
        if (writer != null) {
            writer.flush();
        }
        if (nullWriter != null) {
            nullWriter.flush();
        }
    }

    @Override
    public void release() {
        if (writer != null) {
            writer.release();
            writer = null;
        }
        if (reader != null) {
            reader.release();
            reader = null;
        }
        if (nullWriter != null) {
            nullWriter.release();
            nullWriter = null;
        }
        if (nullReader != null) {
            nullReader.release();
            nullReader = null;
        }
    }
}
