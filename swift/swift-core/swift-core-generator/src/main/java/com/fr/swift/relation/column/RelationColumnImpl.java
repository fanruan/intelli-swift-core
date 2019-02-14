package com.fr.swift.relation.column;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.reliance.IRelationNode;
import com.fr.swift.reliance.RelationNode;
import com.fr.swift.reliance.RelationPathNode;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.RelationColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.Source;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.Task;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskStatusChangeListener;
import com.fr.swift.task.impl.SchedulerTaskImpl;
import com.fr.swift.task.impl.SchedulerTaskPool;
import com.fr.swift.util.Assert;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yee
 * @date 2018/4/3
 */
@SwiftBean(name = "relationColumn")
@SwiftScope("prototype")
public class RelationColumnImpl implements RelationColumn {
    private RelationIndex relationIndex;
    private Segment[] segments;
    private ColumnKey columnKey;
    private DictionaryEncodedColumn[] columns;
    private RelationSource relationSource;
    private int reverseCount;

    /**
     * @param relationIndex 关联索引操作器
     * @param segments      主表的所有segment
     * @param columnKey     主表的column
     */
    public RelationColumnImpl(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey) {
        this.relationIndex = relationIndex;
        this.segments = segments;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.length];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
    }

    public RelationColumnImpl(RelationIndex relationIndex, List<Segment> segments, ColumnKey columnKey) {
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.relationIndex = relationIndex;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
        this.reverseCount = relationIndex.getReverseCount();
        this.relationSource = columnKey.getRelation();
    }

    public RelationColumnImpl(ColumnKey columnKey) {
        Util.requireNonNull(columnKey);
        this.relationSource = columnKey.getRelation();
        Util.requireNonNull(relationSource);
        List<Segment> segments = SwiftContext.get().getBean(SwiftSegmentManager.class).getSegment(relationSource.getPrimarySource());
        Assert.notEmpty(segments);
        this.segments = new Segment[segments.size()];
        this.segments = segments.toArray(this.segments);
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.size()];
    }

    /**
     * 通过子表行号取值
     *
     * @param row
     * @return
     */
    @Override
    public Object getPrimaryValue(int row) {
        int[] result = getPrimarySegAndRow(row);
        if (null != result) {
            if (columns[result[0]] == null) {
                columns[result[0]] = segments[result[0]].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            return columns[result[0]].getValueByRow(result[1]);
        }
        return null;
    }

    @Override
    public Column buildRelationColumn(Segment segment) {
        if (null == relationIndex) {
            buildRelation(segment);
        }
        IResourceLocation baseLocation = relationIndex.getBaseLocation().buildChildLocation("field").buildChildLocation(columnKey.getName());

        Column column = segments[0].getColumn(columnKey);
        Column targetColumn = createColumn(column.getClass(), baseLocation);
        DictionaryEncodedColumn targetDicColumn = targetColumn.getDictionaryEncodedColumn();
        BitmapIndexedColumn bitmapIndexedColumn = targetColumn.getBitmapIndex();
        try {
            targetDicColumn.size();
        } catch (Exception ignore) {
            SwiftLoggers.getLogger(RelationColumnImpl.class).error("Column do not exists! start build this column");
            buildTargetColumn(targetDicColumn, bitmapIndexedColumn);
        }

        return targetColumn;
    }

    private void buildRelation(Segment segment) {
        List<IRelationNode> nodes = new ArrayList<IRelationNode>();
        dealRelationNode(segment.getRelation(RelationPathHelper.convert2CubeRelationPath(relationSource)), nodes, relationSource, null);
        RelationSource dep = null;
        if (!nodes.isEmpty()) {
            dep = relationSource;
        }
        dealRelationNode(segment.getRelation(columnKey, RelationPathHelper.convert2CubeRelationPath(relationSource)), nodes, new FieldRelationSource(columnKey), dep);

        if (!nodes.isEmpty()) {
            int round = CubeTasks.nextRound();

            List<Pair<TaskKey, Object>> pairs = new ArrayList<Pair<TaskKey, Object>>();
            SchedulerTask start = CubeTasks.newStartTask(round),
                    end = CubeTasks.newEndTask(round);

            for (IRelationNode node : nodes) {
                SchedulerTask relationTask = new SchedulerTaskImpl(CubeTasks.newIndexRelationTaskKey(round, node.getNode()));
                List<Source> deps = node.getDepend();
                for (Source d : deps) {
                    SchedulerTask task = SchedulerTaskPool.getInstance().get(CubeTasks.newIndexRelationTaskKey(round, (RelationSource) d));
                    if (null != task) {
                        task.addNext(relationTask);
                    }
                }
                start.addNext(relationTask);
                relationTask.addNext(end);
                pairs.add(new Pair<TaskKey, Object>(relationTask.key(), node.getNode()));
            }
            final CountDownLatch latch = new CountDownLatch(1);
            end.addStatusChangeListener(new TaskStatusChangeListener() {
                @Override
                public void onChange(Task.Status prev, Task.Status now) {
                    if (now == Task.Status.DONE) {
                        latch.countDown();
                    }
                }
            });
            pairs.add(Pair.of(start.key(), null));
            pairs.add(Pair.of(end.key(), null));
            try {
//                CubeTasks.sendTasks(pairs); fixme 这边考虑走indexing 传stuff更新吧
                start.triggerRun();
                latch.await();
            } catch (Exception e) {
                Crasher.crash(e);
            }
        }
        relationIndex = segment.getRelation(columnKey, RelationPathHelper.convert2CubeRelationPath(relationSource));
    }

    private void dealRelationNode(RelationIndex index, List<IRelationNode> nodes, RelationSource relation, RelationSource dep) {
        try {
            index.getIndex(0, 1);
        } catch (Exception e) {
            if (relation.getRelationType() == RelationSourceType.RELATION) {
                nodes.add(new RelationNode(relation, Collections.EMPTY_LIST));
            } else {
                if (null != dep) {
                    nodes.add(new RelationPathNode(relation, Arrays.asList(dep)));
                } else {
                    nodes.add(new RelationPathNode(relation, Collections.EMPTY_LIST));
                }
            }
        }
    }

    private void buildTargetColumn(DictionaryEncodedColumn targetDicColumn, BitmapIndexedColumn bitmapIndexedColumn) {
        int index = 1;
        Set<Integer> globalSet = new HashSet<Integer>();
        for (int i = 0; i < segments.length; i++) {
            if (columns[i] == null) {
                columns[i] = segments[i].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            int size = columns[i].size();

            for (int j = 1; j < size; j++) {
                int global = columns[i].getGlobalIndexByIndex(j);
                if (!globalSet.contains(global)) {
                    try {
                        ImmutableBitMap bitMap = relationIndex.getIndex(i, j);
                        if (!bitMap.isEmpty()) {
                            index = handleDicAndIndex(bitMap, index, targetDicColumn, bitmapIndexedColumn, columns[i].getValue(j));
                        }
                    } catch (Exception ignore) {
                        SwiftLoggers.getLogger(RelationColumnImpl.class).error(ignore);
                    }
                }
            }
        }
        targetDicColumn.putter().putSize(index);
        targetDicColumn.release();
        bitmapIndexedColumn.release();
    }

    private int handleDicAndIndex(ImmutableBitMap bitMap, final int index, final DictionaryEncodedColumn dic, BitmapIndexedColumn indexedColumn, Object dicValue) {
        final AtomicBoolean traversal = new AtomicBoolean(false);
        bitMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                traversal.set(true);
                dic.putter().putIndex(row, index);
            }
        });
        if (traversal.get()) {
            dic.putter().putValue(index, dicValue);
            indexedColumn.putBitMapIndex(index, bitMap);
            return index + 1;
        }
        return index;
    }

    private <T extends Column> T createColumn(Class<T> clazz, IResourceLocation location) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(IResourceLocation.class);
            constructor.setAccessible(true);
            return constructor.newInstance(location);
        } catch (Exception e) {
            return Crasher.crash(e.getMessage(), e);
        }
    }

    @Override
    public int[] getPrimarySegAndRow(int row) {
        if (row < reverseCount) {
            long reverse = relationIndex.getReverseIndex(row);
            if (reverse != NIOConstant.LONG.NULL_VALUE) {
                return reverse2SegAndRow(reverse);
            }
        }
        return null;
    }

    @Override
    public void release() {
        if (null != columns) {
            for (DictionaryEncodedColumn column : columns) {
                column.release();
            }
        }
    }

    /**
     * 根据long来获取segment序号和segment行号
     *
     * @param reverse
     * @return
     */
    private int[] reverse2SegAndRow(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }

}
