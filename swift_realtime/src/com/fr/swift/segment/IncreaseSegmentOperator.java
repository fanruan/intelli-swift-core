package com.fr.swift.segment;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-10 10:54:55
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IncreaseSegmentOperator extends AbstractSegmentOperator {

    protected List<ISegmentHolder> increaseSegmentList;

    public IncreaseSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, List<Segment> segments, String cubeSourceKey) throws SwiftMetaDataException {
        super(sourceKey, metaData, segments, cubeSourceKey);
        this.increaseSegmentList = new ArrayList<ISegmentHolder>();
        if (null != segments && !segments.isEmpty()) {
            for (int i = 0, len = segments.size(); i < len; i++) {
                if (segments.get(i).getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                    this.segmentList.add(new HistorySegmentHolder(segments.get(i)));
                } else {
                    this.segmentList.add(new RealtimeSegmentHolder(segments.get(i)));
                }
            }
        }
    }


    @Override
    public void transport(SwiftResultSet swiftResultSet) throws Exception {
        int count = 0;
        if (metaData.getColumnCount() != 0) {
            String allotColumn = metaData.getColumnName(1);
            while (swiftResultSet.next()) {
                Row row = swiftResultSet.getRowData();
                transportRow(count++, allotColumn, row);
            }
            segmentList.addAll(increaseSegmentList);
        }
    }

    @Override
    public void transportRow(long row, String allotColumn, Row data) throws Exception {
        int index = alloter.allot(row, allotColumn, data.getValue(indexOfColumn(allotColumn)));
        int size = increaseSegmentList.size();
        if (index >= size) {
            for (int i = size; i <= index; i++) {
                increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(segmentList.size() + i)));
            }
        } else if (index == -1) {
            index = increaseSegmentList.size() - 1;
        }
        ISegmentHolder segment = increaseSegmentList.get(index);
        for (int i = 0, len = metaData.getColumnCount(); i < len; i++) {
            try {
                segment.putDetail(i, data.getValue(i));
            } catch (Exception e) {
                segment.putDetail(i, null);
            }
        }
        segment.incrementRowCount();
    }

    @Override
    public void finishTransport() {
//        MetaDataXmlManager.getManager().putMetaData(sourceKey, metaData);
        for (int i = 0, len = increaseSegmentList.size(); i < len; i++) {
            ISegmentHolder holder = increaseSegmentList.get(i);
            holder.putRowCount();
            holder.putAllShowIndex();
            holder.putNullIndex();
            holder.release();
        }
    }

    @Override
    public int getSegmentCount() {
        return 0;
    }

    /**
     * 创建Segment
     * TODO 分块存放目录
     *
     * @param order 块号
     * @return
     * @throws Exception
     * @deprecated fixme 名字换成nextSegment()吧，可能有别的分块规则，和这个不兼容
     */
    @Deprecated
    protected Segment createSegment(int order) throws Exception {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.MEMORY);
        SegmentKey segmentKey = new SegmentKey();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(Types.StoreType.MEMORY);
        SegmentXmlManager.getManager().addSegment(sourceKey, segmentKey);
        return new RealTimeSegmentImpl(location, metaData);
    }
}
