package com.fr.swift.generate;


import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.BitMapOrHelper;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public abstract class BaseTablePathIndexer extends BaseWorker {
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
            build();
            workOver(Task.Result.SUCCEEDED);
        } catch (Exception e) {
            LOGGER.error("Build index error with exception!", e);
            workOver(Task.Result.FAILED);
        }
    }

    private void build() {
        if (relationPath.size() > 1) {
            LOGGER.info("Start build TablePathIndex: " + relationPath.getKey());
            List<Segment> primary = getPrimaryTableSegments();
            List<Segment> pre = getPreTableSegments();
            List<Segment> target = getTargetTableSegments();
            for (int i = 0, primaryLen = primary.size(); i < primaryLen; i++) {
                Segment pSeg = primary.get(i);
                for (int j = 0, preLen = pre.size(); j < preLen; j++) {
                    Segment preSeg = pre.get(j);
                    for (int k = 0, targetLen = target.size(); k < targetLen; k++) {
                        LOGGER.info(String.format("Start build primary segment: [%d], pre segment: [%d], target segment: [%d]", i, j, k));
                        buildIndexPerSegment(pSeg, preSeg, target.get(k));
                        LOGGER.info(String.format("Build primary segment: [%d], pre segment: [%d], target segment: [%d] success", i, j, k));
                    }
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
        CubeMultiRelationPath result = new CubeMultiRelationPath();
        result.copyFrom(relationPath);
        result.removeLastRelation();
        return result;
    }

    protected abstract List<Segment> getSegments(SourceKey key);

    List<Segment> getPrimaryTableSegments() {
        return getSegments(getPrimaryTable());
    }

    private List<Segment> getPreTableSegments() {
        return getSegments(getPreTable());
    }

    List<Segment> getTargetTableSegments() {
        return getSegments(getTargetTable());
    }

    private void buildIndexPerSegment(Segment primaryTableSegment, Segment preTableSegment, Segment targetTableSegment) {
        RelationIndex preTableRelationIndex = preTableSegment.getRelation(getPrePath());
        RelationIndex lastRelationReader = targetTableSegment.getRelation(relationPath.getLastRelation());
        RelationIndex targetTableRelationIndex = targetTableSegment.getRelation(relationPath);
        try {
            int rowCount = primaryTableSegment.getRowCount();
            IntArray reverse = IntListFactory.createIntArray(targetTableSegment.getRowCount(), NIOConstant.INTEGER.NULL_VALUE);
            BitMapOrHelper helper = new BitMapOrHelper();
            for (int i = 0; i < rowCount; i++) {
                ImmutableBitMap preTableIndex = preTableRelationIndex.getIndex(i + 1);
                ImmutableBitMap resultIndex = getTableLinkedOrGVI(preTableIndex, lastRelationReader);
                helper.add(resultIndex);
                targetTableRelationIndex.putIndex(i + 1, resultIndex);
                initReverse(reverse, i, resultIndex);
            }
            buildReverseIndex(targetTableRelationIndex, reverse);
            targetTableRelationIndex.putNullIndex(helper.compute().getNot(reverse.size()));
        } catch (Exception e) {
            Crasher.crash(e);
        } finally {
            if (null != preTableRelationIndex) {
                preTableRelationIndex.release();
            }

            if (null != lastRelationReader) {
                lastRelationReader.release();
            }

            if (null != targetTableRelationIndex) {
                targetTableRelationIndex.release();
            }
            primaryTableSegment.release();
            preTableSegment.release();
            targetTableSegment.release();
        }
    }

    private void buildReverseIndex(RelationIndex targetTableRelationIndex, IntArray reverse) {
        for (int i = 0, len = reverse.size(); i < len; i++) {
            targetTableRelationIndex.putReverseIndex(i + 1, reverse.get(i));
        }
    }

    ImmutableBitMap getTableLinkedOrGVI(ImmutableBitMap currentIndex, final RelationIndex relationIndex) {
        if (null != currentIndex) {
            final List<ImmutableBitMap> bitMaps = new ArrayList<ImmutableBitMap>();
            currentIndex.breakableTraversal(new BreakTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    try {
                        bitMaps.add(relationIndex.getIndex(row + 1));
                    } catch (Exception ignore) {
                        bitMaps.add(BitMaps.EMPTY_IMMUTABLE);
                    }
                    return false;
                }
            });
            ImmutableBitMap result = BitMaps.newRoaringMutable();
            for (ImmutableBitMap bitMap : bitMaps) {
                result = result.getOr(bitMap);
            }
            return result;
        }
        return null;
    }

    private void initReverse(final IntArray reverse, final int indexRow, ImmutableBitMap index) {
        index.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                reverse.put(row, indexRow);
                return false;
            }
        });
    }
}
