package com.fr.swift.generate.history.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseFieldPathIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class FieldPathIndexer extends BaseFieldPathIndexer {
    public FieldPathIndexer(CubeMultiRelationPath relationPath, ColumnKey logicColumnKey, SwiftSegmentManager provider) {
        super(relationPath, logicColumnKey, provider);
    }

    @Override
    protected List<Segment> getSegments(SourceKey key) {
        return provider.getSegment(key);
    }

    @Override
    protected void releaseIfNeed(Releasable releasable) {
        if (null != releasable) {
            releasable.release();
        }
    }
}
