package com.fr.swift.segment.relation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.BitMapReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.BitMapWriter;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.LongWriter;

/**
 * @author anchore
 * @date 2018/1/17
 */
public class RelationIndexImpl implements RelationIndex {
    private static final String NULL_INDEX = "null_index";
    private static final String INDEX = "index";
    private static final String REVERSE = "reverse";
    private static final String RELATIONS_KEY = "relations";
    private static final String SEG_INDEX = "seg_index";
    private static final String REVERSE_COUNT = "reverse_count";

    private static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    private IResourceLocation location;
    private BitMapWriter indexWriter, nullIndexWriter;
    private LongWriter reverseIndexWriter;
    private BitMapReader indexReader, nullIndexReader;
    private LongReader reverseIndexReader;
    private IntWriter segIndexWriter;
    private IntReader segIndexReader;
    private IntWriter reverseCountWriter;
    private IntReader reverseCountReader;

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
            indexWriter = DISCOVERY.getWriter(location.buildChildLocation(INDEX), new BuildConf(IoType.WRITE, DataType.BITMAP));
        }
        indexWriter.put(pos, bitmap);
    }

    @Override
    public ImmutableBitMap getIndex(int pos) {
        if (indexReader == null) {
            indexReader = DISCOVERY.getReader(location.buildChildLocation(INDEX), new BuildConf(IoType.READ, DataType.BITMAP));
        }
        return indexReader.get(pos);
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
    public void putSegIndex(int fPos, int tPos) {
        if (segIndexWriter == null) {
            segIndexWriter = DISCOVERY.getWriter(location.buildChildLocation(SEG_INDEX), new BuildConf(IoType.WRITE, DataType.INT));
        }
        segIndexWriter.put(tPos, fPos);
    }

    @Override
    public int getSegIndex(int fPos) {
        if (segIndexReader == null) {
            segIndexReader = DISCOVERY.getReader(location.buildChildLocation(SEG_INDEX), new BuildConf(IoType.READ, DataType.INT));
        }
        return segIndexReader.get(fPos);
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
        if (segIndexWriter != null) {
            segIndexWriter.release();
            segIndexWriter = null;
        }
        if (reverseCountWriter != null) {
            reverseCountWriter.release();
            reverseCountWriter = null;
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
        if (reverseCountReader != null) {
            reverseCountReader.release();
            reverseCountReader = null;
        }
        if (segIndexReader != null) {
            segIndexReader.release();
            segIndexReader = null;
        }
        if (nullIndexReader != null) {
            nullIndexReader.release();
            nullIndexReader = null;
        }
    }
}
