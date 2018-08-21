package com.fr.swift.segment.column.impl.base;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;

/**
 * @author anchore
 * @date 2017/11/9
 */
public class BitMapColumn extends BaseBitmapColumn {
    private static final String INDEX = "index";

    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    private BitMapWriter indexWriter;
    private BitMapReader indexReader;
    private IResourceLocation indexLocation;

    public BitMapColumn(IResourceLocation parent) {
        this.indexLocation = parent.buildChildLocation(INDEX);
    }

    private void initIndexWriter() {
        if (indexWriter == null) {
            indexWriter = DISCOVERY.getWriter(indexLocation, new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
    }

    private void initIndexReader() {
        if (indexReader == null) {
            indexReader = DISCOVERY.getReader(indexLocation, new BuildConf(IoType.READ, DataType.BITMAP));
        }
    }

    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        initIndexWriter();
        indexWriter.put(index, bitmap);
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        initIndexReader();
        return indexReader.get(index);
    }

    @Override
    public void flush() {
        if (indexWriter != null) {
            indexWriter.flush();
        }
    }

    @Override
    public void release() {
        if (indexWriter != null) {
            indexWriter.release();
            indexWriter = null;
        }
        if (indexReader != null) {
            indexReader.release();
            indexReader = null;
        }
    }
}
