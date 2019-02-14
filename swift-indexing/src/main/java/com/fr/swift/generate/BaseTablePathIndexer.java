package com.fr.swift.generate;


import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.FasterAggregation;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.column.SwiftTablePathIndexer;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.segment.relation.impl.CubeMultiRelationPathImpl;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.impl.BaseWorker;
import com.fr.swift.task.impl.TaskResultImpl;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public abstract class BaseTablePathIndexer extends BaseWorker implements SwiftTablePathIndexer {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseTablePathIndexer.class);

    CubeMultiRelationPath relationPath;
    protected SwiftSegmentManager provider;

    public BaseTablePathIndexer(CubeMultiRelationPath relationPath, SwiftSegmentManager provider) {
        this.relationPath = relationPath;
        this.provider = provider;
    }

    @Override
    public void work() {
        try {
            buildTablePath();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            LOGGER.error("Build index error with exception!", e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void buildTablePath() {
        if (relationPath.size() > 1) {
            LOGGER.info("Start build TablePathIndex: " + relationPath.getKey());
            List<Segment> primary = getPrimaryTableSegments();
            List<Segment> pre = getPreTableSegments();
            List<Segment> target = getTargetTableSegments();
            for (int i = 0; i < target.size(); i++) {
                Segment tSegment = target.get(i);
                RelationIndex lastRelationReader = tSegment.getRelation(relationPath.getLastRelation());
                RelationIndex targetTableRelationIndex = tSegment.getRelation(relationPath);
                RelationIndex preTableRelationIndex = null;
                Segment preSegment = null;
                try {
                    int reversePos = 0;
                    for (int j = 0; j < pre.size(); j++) {
                        preSegment = pre.get(j);
                        preTableRelationIndex = preSegment.getRelation(getPrePath());
                        for (int k = 0; k < primary.size(); k++) {
                            Segment pSegment = primary.get(k);
                            reversePos = buildIndexPerSegment(preTableRelationIndex, targetTableRelationIndex, lastRelationReader, tSegment.getRowCount(), pSegment.getRowCount(), reversePos, k);
                            releaseIfNeed(pSegment);
                        }
                    }
                    targetTableRelationIndex.putReverseCount(reversePos);
                } catch (Exception e) {
                    Crasher.crash("build path index error", e);
                } finally {
                    releaseIfNeed(preTableRelationIndex);
                    releaseIfNeed(lastRelationReader);
                    releaseIfNeed(targetTableRelationIndex);
                    releaseIfNeed(tSegment);
                    releaseIfNeed(preSegment);
                }
            }
        }
    }

    private SourceKey getPrimaryTable() {
        return relationPath.getStartTable();
    }

    private SourceKey getTargetTable() {
        return relationPath.getEndTable();
    }

    private SourceKey getPreTable() {
        return getPrePath().getEndTable();
    }

    private CubeMultiRelationPath getPrePath() {
        CubeMultiRelationPath result = new CubeMultiRelationPathImpl();
        result.copyFrom(relationPath);
        result.removeLastRelation();
        return result;
    }

    protected abstract List<Segment> getSegments(SourceKey key);

    protected abstract void releaseIfNeed(Releasable releasable);

    List<Segment> getPrimaryTableSegments() {
        return getSegments(getPrimaryTable());
    }

    private List<Segment> getPreTableSegments() {
        return getSegments(getPreTable());
    }

    List<Segment> getTargetTableSegments() {
        return getSegments(getTargetTable());
    }

    /**
     * @param preTableRelationIndex
     * @param targetTableRelationIndex
     * @param lastRelationReader
     * @param reverseSize
     * @param totalPos
     * @param reversePos
     * @return
     */
    private int buildIndexPerSegment(RelationIndex preTableRelationIndex, RelationIndex targetTableRelationIndex, RelationIndex lastRelationReader, int reverseSize, int totalPos, int reversePos, int primaryIndex) {

        try {
            LongArray reverse = LongListFactory.createLongArray(reverseSize, NIOConstant.LONG.NULL_VALUE);
            List<ImmutableBitMap> bitmaps = new ArrayList<ImmutableBitMap>();
            for (int i = 0; i < totalPos; i++) {
                ImmutableBitMap preTableIndex = preTableRelationIndex.getIndex(primaryIndex, i + 1);
                ImmutableBitMap resultIndex = getTableLinkedOrGVI(preTableIndex, lastRelationReader, primaryIndex);
                bitmaps.add(resultIndex);
                targetTableRelationIndex.putIndex(primaryIndex, i + 1, resultIndex);
                initReverse(reverse, i, resultIndex, primaryIndex);
            }
            targetTableRelationIndex.putNullIndex(primaryIndex, FasterAggregation.or(bitmaps).getNot(reverse.size()));
            return buildReverseIndex(targetTableRelationIndex, reverse, reversePos);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    private int buildReverseIndex(RelationIndex targetTableRelationIndex, LongArray reverse, int reversePos) {
        for (int i = 0, len = reverse.size(); i < len; i++) {
            targetTableRelationIndex.putReverseIndex(reversePos++, reverse.get(i));
        }
        return reversePos;
    }

    MutableBitMap getTableLinkedOrGVI(ImmutableBitMap currentIndex, final RelationIndex relationIndex, final int primaryIndex) {
        if (null != currentIndex) {
            final List<ImmutableBitMap> bitMaps = new ArrayList<ImmutableBitMap>();
            currentIndex.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    try {
                        bitMaps.add(relationIndex.getIndex(primaryIndex, row + 1));
                    } catch (Exception ignore) {
                        bitMaps.add(BitMaps.newRoaringMutable());
                    }
                }
            });
            MutableBitMap result = BitMaps.newRoaringMutable();
            for (ImmutableBitMap bitMap : bitMaps) {
                result.or(bitMap);
            }
            return result;
        }
        return null;
    }

    private void initReverse(final LongArray reverse, final int indexRow, ImmutableBitMap index, final int primaryIndex) {
        index.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                reverse.put(row, RelationIndexHelper.merge2Long(primaryIndex, indexRow));
            }
        });
    }
}
