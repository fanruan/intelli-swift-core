package com.fr.swift.segment.relation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;

/**
 * @author anchore
 * @date 2018/1/17
 */
public class RelationIndexImpl implements RelationIndex {
    private static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    private IResourceLocation location;
    private BitMapWriter indexWriter, nullIndexWriter;
    private IntWriter reverseIndexWriter;
    private BitMapReader indexReader, nullIndexReader;
    private IntReader reverseIndexReader;

    private RelationIndexImpl(IResourceLocation baseLocation, String primaryTable, String relationKey) {
        this.location = baseLocation.buildChildLocation(primaryTable).buildChildLocation(relationKey);
    }

    public static RelationIndexImpl newRelationIndex(IResourceLocation baseLocation, String primaryTable, String relationKey) {
        return new RelationIndexImpl(baseLocation.buildChildLocation(RELATIONS_KEY), primaryTable, relationKey);
    }

    public static RelationIndexImpl newFieldRelationIndex(IResourceLocation baseLocation, String primaryTable, String relationKey) {
        IResourceLocation location = baseLocation.buildChildLocation(RELATIONS_KEY);
        return new RelationIndexImpl(location.buildChildLocation("field"), primaryTable, relationKey);
    }

    @Override
    public void putIndex(int pos, ImmutableBitMap bitmap) {
        if (indexWriter == null) {
            indexWriter = (BitMapWriter) DISCOVERY.getWriter(location.buildChildLocation(INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
        indexWriter.put(pos, bitmap);
    }

    @Override
    public ImmutableBitMap getIndex(int pos) {
        if (indexReader == null) {
            indexReader = (BitMapReader) DISCOVERY.getReader(location.buildChildLocation(INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
        return indexReader.get(pos);
    }

    @Override
    public void putNullIndex(ImmutableBitMap bitmap) {
        if (nullIndexWriter == null) {
            nullIndexWriter = (BitMapWriter) DISCOVERY.getWriter(location.buildChildLocation(NULL_INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
        nullIndexWriter.put(0, bitmap);
    }

    @Override
    public ImmutableBitMap getNullIndex() {
        if (nullIndexReader == null) {
            nullIndexReader = (BitMapReader) DISCOVERY.getReader(location.buildChildLocation(NULL_INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
        return nullIndexReader.get(0);
    }

    @Override
    public void putReverseIndex(int tPos, int fPos) {
        if (reverseIndexWriter == null) {
            reverseIndexWriter = (IntWriter) DISCOVERY.getWriter(location.buildChildLocation(REVERSE), new BuildConf(IoType.WRITE, DataType.INT));
        }
        reverseIndexWriter.put(tPos, fPos);
    }

    @Override
    public int getReverseIndex(int fPos) {
        if (reverseIndexReader == null) {
            reverseIndexReader = (IntReader) DISCOVERY.getReader(location.buildChildLocation(REVERSE), new BuildConf(IoType.READ, DataType.INT));
        }
        return reverseIndexReader.get(fPos);
    }

    @Override
    public void flush() {
        if (indexWriter != null) {
            indexWriter.flush();
        }
        if (reverseIndexWriter != null) {
            reverseIndexWriter.flush();
        }
        if (nullIndexWriter != null) {
            nullIndexWriter.flush();
        }
    }

    @Override
    public void release() {
        if (indexWriter != null) {
            indexWriter.release();
            indexWriter = null;
        }
        if (reverseIndexWriter != null) {
            reverseIndexWriter.release();
            reverseIndexWriter = null;
        }
        if (nullIndexWriter != null) {
            nullIndexWriter.release();
            nullIndexWriter = null;
        }
        if (indexReader != null) {
            indexReader.release();
            indexReader = null;
        }
        if (reverseIndexReader != null) {
            reverseIndexReader.release();
            reverseIndexReader = null;
        }
        if (nullIndexReader != null) {
            nullIndexReader.release();
            nullIndexReader = null;
        }
    }
}
