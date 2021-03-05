package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.BuildConf;
import com.fr.swift.cloud.cube.io.Types.DataType;
import com.fr.swift.cloud.cube.io.Types.IoType;
import com.fr.swift.cloud.cube.io.input.BitMapReader;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.cube.io.output.BitMapWriter;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.util.IoUtil;

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
    public void putNullIndex(ImmutableBitMap bitMap) {
        initIndexWriter();
        indexWriter.resetContentPosition();
        indexWriter.put(0, bitMap);
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        initIndexReader();
        return indexReader.get(index);
    }

    @Override
    public boolean isReadable() {
        initIndexReader();
        boolean readable = indexReader.isReadable();
        if (indexLocation.getStoreType().isPersistent()) {
            IoUtil.release(indexReader);
        }
        indexReader = null;
        return readable;
    }

    @Override
    public void release() {
        IoUtil.release(indexWriter, indexReader);
        indexWriter = null;
        indexReader = null;
        SwiftLoggers.getLogger().debug("swift bitmap released at {}", indexLocation.getPath());
    }
}
