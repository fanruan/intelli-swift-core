package com.fr.swift.generate;


import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.generate.history.index.RelationIndexHelper;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.util.Crasher;

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
            int primaryPos = 0;
            for (Segment segment : primary) {
                primaryPos += segment.getRowCount();
                releaseIfNeed(segment);
            }
//            primaryPos += primary.size();
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
                        reversePos = buildIndexPerSegment(preTableRelationIndex, targetTableRelationIndex, lastRelationReader, tSegment.getRowCount(), primaryPos, reversePos);
                    }
                    targetTableRelationIndex.putReverseCount(reversePos);
                } catch (Exception e) {
                    LOGGER.error("build path index error", e);
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
        CubeMultiRelationPath result = new CubeMultiRelationPath();
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
     * TODO NullIndex暂时没想好
     * @param preTableRelationIndex
     * @param targetTableRelationIndex
     * @param lastRelationReader
     * @param reverseSize
     * @param totalPos
     * @param reversePos
     * @return
     */
    private int buildIndexPerSegment(RelationIndex preTableRelationIndex, RelationIndex targetTableRelationIndex, RelationIndex lastRelationReader, int reverseSize, int totalPos, int reversePos) {

        try {
            LongArray reverse = LongListFactory.createLongArray(reverseSize, NIOConstant.LONG.NULL_VALUE);
//            BitMapOrHelper helper = new BitMapOrHelper();
            for (int i = 0; i < totalPos; i++) {
                LongArray preTableIndex = preTableRelationIndex.getIndex(i + 1);
                LongArray resultIndex = getTableLinkedOrGVI(preTableIndex, lastRelationReader);
//                helper.add(resultIndex);
                targetTableRelationIndex.putIndex(i + 1, resultIndex);
                initReverse(reverse, i, resultIndex);
            }
            return buildReverseIndex(targetTableRelationIndex, reverse, reversePos);
//            targetTableRelationIndex.putNullIndex(0, helper.compute().getNot(reverse.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return Crasher.crash(e);
        }
    }

    private int buildReverseIndex(RelationIndex targetTableRelationIndex, LongArray reverse, int reversePos) {
        for (int i = 0, len = reverse.size(); i < len; i++) {
            targetTableRelationIndex.putReverseIndex(reversePos++, reverse.get(i));
        }
        return reversePos;
    }

    LongArray getTableLinkedOrGVI(LongArray currentIndex, final RelationIndex relationIndex) {
        RelationIndexHelper helper = new RelationIndexHelper();
        if (null != currentIndex) {
            for (int i = 0; i < currentIndex.size(); i++) {
                long value = currentIndex.get(i);
                if (value != NIOConstant.LONG.NULL_VALUE) {
                    int[] result = RelationIndexHelper.reverse2SegAndRow(value);
                    try {
                        helper.addNullIndex(relationIndex.getIndex(result[1] + 1));
                    } catch (Exception ignore) {
                        helper.addNullIndex(LongListFactory.createLongArray(1, NIOConstant.LONG.NULL_VALUE));
                    }
                }
            }
            return helper.getNullIndex();
        }
        return LongListFactory.createLongArray(1, NIOConstant.LONG.NULL_VALUE);
    }

    private void initReverse(final LongArray reverse, final int indexRow, LongArray index) {
        for (int i = 0; i < index.size(); i++) {
            long value = index.get(i);
            if (value != NIOConstant.LONG.NULL_VALUE) {
                int[] result = RelationIndexHelper.reverse2SegAndRow(value);
                reverse.put(result[1], RelationIndexHelper.merge2Long(result[0], indexRow));
            }
        }
    }
}
