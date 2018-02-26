package com.fr.swift.segment;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.MetaDataXmlManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/4
 */
public class HistorySegmentOperator extends AbstractSegmentOperator {

    public HistorySegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, List<Segment> segments, String cubeSourceKey) throws SwiftMetaDataException {
        super(sourceKey, metaData, segments, cubeSourceKey);
        if (null != segments && !segments.isEmpty()) {
            for (int i = 0, len = segments.size(); i < len; i++) {
                this.segmentList.add(new HistorySegmentHolder(segments.get(i)));
            }
        }
    }

    /**
     * 全量更新分块
     *
     * @param row
     * @param allotColumn
     * @param data
     * @throws Exception
     */
    @Override
    public void transportRow(long row, String allotColumn, Row data) throws Exception {
        int index = alloter.allot(row, allotColumn, data.getValue(indexOfColumn(allotColumn)));
        int size = segmentList.size();
        if (index >= size) {
            for (int i = size; i <= index; i++) {
                segmentList.add(new HistorySegmentHolder(createSegment(i)));
            }
        } else if (index == -1) {
            index = segmentList.size() - 1;
        }
        ISegmentHolder segment = segmentList.get(index);
        for (int i = 0, len = (metaData.getColumnCount() <= data.getSize() ? metaData.getColumnCount() : data.getSize()); i < len; i++) {
            segment.putDetail(i, data.getValue(i));
        }
        segment.incrementRowCount();
    }

    /**
     * 全量更新分块
     *
     * @param swiftResultSet
     * @throws Exception
     */
    @Override
    public void transport(SwiftResultSet swiftResultSet) throws Exception {
        int count = 0;
        String allotColumn = metaData.getColumnName(1);
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();
            transportRow(count++, allotColumn, row);
        }
    }

    @Override
    public void finishTransport() {
        MetaDataXmlManager.getManager().putMetaData(sourceKey, metaData);
        for (int i = 0, len = segmentList.size(); i < len; i++) {
            ISegmentHolder holder = segmentList.get(i);
            holder.putRowCount();
            holder.putAllShowIndex();
            holder.putNullIndex();
            holder.release();
        }
    }

    @Override
    public int getSegmentCount() {
        return segmentList.size();
    }

    /**
     * 创建Segment
     * TODO 分块存放目录
     *
     * @param order 块号
     * @return
     * @throws Exception
     */
    private Segment createSegment(int order) throws Exception {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath);
        SegmentKey segmentKey = new SegmentKey();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(Types.StoreType.FINE_IO);
        SegmentXmlManager.getManager().addSegment(sourceKey, segmentKey);
        return new HistorySegmentImpl(location, metaData);
    }


}
