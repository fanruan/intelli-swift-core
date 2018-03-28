//package com.fr.swift.generate.merge;
//
//import com.fr.general.ComparatorUtils;
//import com.fr.swift.config.IConfigSegment;
//import com.fr.swift.config.IMetaData;
//import com.fr.swift.config.ISegmentKey;
//import com.fr.swift.config.conf.MetaDataConfig;
//import com.fr.swift.config.conf.MetaDataConvertUtil;
//import com.fr.swift.config.conf.SegmentConfig;
//import com.fr.swift.config.unique.SegmentKeyUnique;
//import com.fr.swift.config.unique.SegmentUnique;
//import com.fr.swift.cube.io.Types;
//import com.fr.swift.cube.io.location.IResourceLocation;
//import com.fr.swift.cube.io.location.ResourceLocation;
//import com.fr.swift.exception.meta.SwiftMetaDataException;
//import com.fr.swift.log.SwiftLogger;
//import com.fr.swift.log.SwiftLoggers;
//import com.fr.swift.manager.LocalSegmentProvider;
//import com.fr.swift.segment.HistorySegmentImpl;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.column.ColumnKey;
//import com.fr.swift.segment.column.DetailColumn;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.SwiftMetaData;
//import com.fr.swift.source.SwiftSourceAlloter;
//import com.fr.swift.source.SwiftSourceAlloterFactory;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This class created on 2018/3/20
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class MergeSegmentOperator implements SegmentOperator {
//
//    private SwiftLogger LOGGER = SwiftLoggers.getLogger(MergeSegmentOperator.class);
//
//    private SourceKey sourceKey;
//    private SwiftMetaData metaData;
//    private String cubeSourceKey;
//    protected SwiftSourceAlloter alloter;
//
//    private List<SegmentHolder> historySegmentList = new ArrayList<SegmentHolder>();
//    private List<SegmentHolder> realtimeSegmentList = new ArrayList<SegmentHolder>();
//    private List<SegmentHolder> newHisSegmentList = new ArrayList<SegmentHolder>();
//
//    IConfigSegment configSegment;
//
//    private int historyCount = 0;
//    private int realtimeCount = 0;
//
//    public MergeSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, String cubeSourceKey) throws SQLException {
//        this.sourceKey = sourceKey;
//        this.metaData = metaData;
//        this.alloter = SwiftSourceAlloterFactory.createSourceAlloter(sourceKey);
//        this.cubeSourceKey = cubeSourceKey;
//
//        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(sourceKey);
//        configSegment = new SegmentUnique();
//        this.configSegment.setSourceKey(sourceKey.getId());
//
//        for (int i = 0; i < segmentList.size(); i++) {
//            Segment segment = segmentList.get(i);
//            if (ComparatorUtils.equals(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO)) {
//                historyCount += segment.getRowCount();
//                historySegmentList.add(new HistorySegmentHolder(segment));
//                createSegment(i);
//            } else {
//                realtimeSegmentList.add(new RealtimeSegmentHolder(segment));
//                realtimeCount += segment.getRowCount();
//            }
//        }
//    }
//
//    @Override
//    public void transport() throws Exception {
//        int count = 0;
//        String allotColumn = metaData.getColumnName(1);
//        for (SegmentHolder segment : realtimeSegmentList) {
//            int currentCount = 0;
//            int segCount = segment.getSegment().getRowCount();
//            int columnCount = metaData.getColumnCount();
//
//            List<DetailColumn> columnList = new ArrayList<DetailColumn>();
//            for (int i = 0; i < columnCount; i++) {
//                columnList.add(segment.getColumn(new ColumnKey(metaData.getColumnName(i + 1))));
//            }
//
//            while (currentCount < segCount) {
//                int index = alloter.allot(count++, allotColumn, null);
//                if (index >= newHisSegmentList.size()) {
//                    for (int i = newHisSegmentList.size(); i <= index; i++) {
//                        newHisSegmentList.add(new HistorySegmentHolder(createSegment(i + historySegmentList.size())));
//                    }
//                } else if (index == -1) {
//                    index = newHisSegmentList.size() - 1;
//                }
//                SegmentHolder segmentHolder = newHisSegmentList.get(index);
//                for (int i = 0; i < columnCount; i++) {
//                    segmentHolder.putDetail(i, columnList.get(i).get(currentCount));
//                }
//                currentCount++;
//                segmentHolder.incrementRowCount();
//            }
//        }
//        historySegmentList.addAll(newHisSegmentList);
//    }
//
//    @Override
//    public void finishTransport() {
//        try {
//            IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(this.metaData);
//            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
//        } catch (SwiftMetaDataException e) {
//            LOGGER.error("save metadata failed! ", e);
//        }
//        for (int i = 0, len = newHisSegmentList.size(); i < len; i++) {
//            SegmentHolder holder = newHisSegmentList.get(i);
//            holder.putRowCount();
//            holder.putAllShowIndex();
//            holder.putNullIndex();
//            holder.release();
//        }
//        SegmentConfig.getInstance().putSegments(configSegment);
//    }
//
//    @Override
//    public List<String> getIndexFields() throws SwiftMetaDataException {
//        List<String> fields = new ArrayList<String>();
//        for (int i = 1; i <= metaData.getColumnCount(); i++) {
//            fields.add(metaData.getColumnName(i));
//        }
//        return fields;
//    }
//
//    protected Segment createSegment(int order) {
//        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
//        IResourceLocation location = new ResourceLocation(cubePath);
//        ISegmentKey segmentKey = new SegmentKeyUnique();
//        segmentKey.setSegmentOrder(order);
//        segmentKey.setUri(location.getUri().getPath());
//        segmentKey.setSourceId(sourceKey.getId());
//        segmentKey.setStoreType(Types.StoreType.FINE_IO.name());
//        configSegment.addSegment(segmentKey);
//        return new HistorySegmentImpl(location, metaData);
//    }
//}
