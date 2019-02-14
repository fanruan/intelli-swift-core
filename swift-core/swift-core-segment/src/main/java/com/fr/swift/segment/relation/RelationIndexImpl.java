package com.fr.swift.segment.relation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.segment.column.impl.base.IResourceDiscovery;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/17
 */
public class RelationIndexImpl implements RelationIndex {
    private static final String NULL_INDEX = "null_index";
    private static final String INDEX = "index";
    private static final String REVERSE = "reverse";
    public static final String RELATIONS_KEY = "relations";
    private static final String REVERSE_COUNT = "reverse_count";

    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    private IResourceLocation location;
    private IResourceLocation baseLocation;
    private BitMapWriter nullIndexWriter;
    private LongWriter reverseIndexWriter;
    private BitMapReader nullIndexReader;
    private LongReader reverseIndexReader;
    private IntWriter reverseCountWriter;
    private IntReader reverseCountReader;

    private List<BitMapReader> indexReaders;
    private List<BitMapWriter> indexWriters;

    private RelationIndexImpl(IResourceLocation baseLocation, String child, String primaryTable, String relationKey) {
        this.baseLocation = baseLocation;
        this.location = baseLocation.buildChildLocation(RELATIONS_KEY);
        if (!Strings.isEmpty(child)) {
            this.location = this.location.buildChildLocation(child);
        }
        this.location = this.location.buildChildLocation(primaryTable).buildChildLocation(relationKey);
        indexWriters = new ArrayList<BitMapWriter>();
        indexReaders = new ArrayList<BitMapReader>();
    }

    public static RelationIndexImpl newRelationIndex(IResourceLocation baseLocation, String primaryTable, String relationKey) {
        return new RelationIndexImpl(baseLocation.buildChildLocation(RELATIONS_KEY), null, primaryTable, relationKey);
    }

    public static RelationIndexImpl newFieldRelationIndex(IResourceLocation baseLocation, String primaryTable, String relationKey) {
        return new RelationIndexImpl(baseLocation, "field", primaryTable, relationKey);
    }

    @Override
    public void putIndex(int segIndex, int pos, ImmutableBitMap bitMap) {
        if (indexWriters.size() <= segIndex) {
            indexWriters.add((BitMapWriter) DISCOVERY.getWriter(location.buildChildLocation(INDEX).buildChildLocation(String.valueOf(segIndex)), new BuildConf(IoType.WRITE, DataType.BITMAP)));
        }
        indexWriters.get(segIndex).put(pos, bitMap);
    }

    @Override
    public ImmutableBitMap getIndex(int segIndex, int pos) {
        if (indexReaders.size() <= segIndex) {
            indexReaders.add((BitMapReader) DISCOVERY.getReader(location.buildChildLocation(INDEX).buildChildLocation(String.valueOf(segIndex)), new BuildConf(IoType.READ, DataType.BITMAP)));
        }
        return indexReaders.get(segIndex).get(pos);
    }

    @Override
    public void putNullIndex(int pos, ImmutableBitMap bitmap) {
        if (nullIndexWriter == null) {
            nullIndexWriter = DISCOVERY.getWriter(location.buildChildLocation(NULL_INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
        nullIndexWriter.put(pos, bitmap);
    }

    @Override
    public ImmutableBitMap getNullIndex(int pos) {
        if (nullIndexReader == null) {
            nullIndexReader = DISCOVERY.getReader(location.buildChildLocation(NULL_INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
        return nullIndexReader.get(pos);
    }

    @Override
    public void putReverseIndex(int tPos, long fPos) {
        if (reverseIndexWriter == null) {
            reverseIndexWriter = DISCOVERY.getWriter(location.buildChildLocation(REVERSE), new BuildConf(IoType.WRITE, DataType.LONG));
        }
        reverseIndexWriter.put(tPos, fPos);
    }

    @Override
    public long getReverseIndex(int fPos) {
        if (reverseIndexReader == null) {
            reverseIndexReader = DISCOVERY.getReader(location.buildChildLocation(REVERSE), new BuildConf(IoType.READ, DataType.LONG));
        }
        return reverseIndexReader.get(fPos);
    }

    @Override
    public int getReverseCount() {
        if (reverseCountReader == null) {
            reverseCountReader = DISCOVERY.getReader(location.buildChildLocation(REVERSE_COUNT), new BuildConf(IoType.READ, DataType.INT));
        }
        return reverseCountReader.get(0);
    }

    @Override
    public void putReverseCount(int count) {
        if (reverseCountWriter == null) {
            reverseCountWriter = DISCOVERY.getWriter(location.buildChildLocation(REVERSE_COUNT), new BuildConf(IoType.WRITE, DataType.INT));
        }
        reverseCountWriter.put(0, count);
    }

    @Override
    public IResourceLocation getBaseLocation() {
        return baseLocation;
    }


    @Override
    public void flush() {
        if (reverseIndexWriter != null) {
            reverseIndexWriter.flush();
        }
        if (nullIndexWriter != null) {
            nullIndexWriter.flush();
        }
    }

    @Override
    public void release() {
        if (reverseIndexWriter != null) {
            reverseIndexWriter.release();
            reverseIndexWriter = null;
        }
        if (reverseCountWriter != null) {
            reverseCountWriter.release();
            reverseCountWriter = null;
        }
        if (nullIndexWriter != null) {
            nullIndexWriter.release();
            nullIndexWriter = null;
        }
        if (reverseIndexReader != null) {
            reverseIndexReader.release();
            reverseIndexReader = null;
        }
        if (reverseCountReader != null) {
            reverseCountReader.release();
            reverseCountReader = null;
        }
        if (nullIndexReader != null) {
            nullIndexReader.release();
            nullIndexReader = null;
        }

        for (BitMapReader read : indexReaders) {
            read.release();
        }
        indexReaders.clear();
        for (BitMapWriter write : indexWriters) {
            write.release();
        }
        indexWriters.clear();
    }
}
