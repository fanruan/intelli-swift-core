package com.fr.swift.segment.operator.insert;

import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Recorder;
import com.fr.swift.segment.operator.record.RealtimeRecorder;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeBlockSwiftInserter extends AbstractBlockInserter {

    private Recorder recorder;

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData);
        recorder = new RealtimeRecorder(sourceKey, swiftMetaData, fields, cubeSourceKey);
    }

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData, fields);
        recorder = new RealtimeRecorder(sourceKey, swiftMetaData, fields, cubeSourceKey);

    }

    @Override
    protected Segment createNewSegment(IResourceLocation location, SwiftMetaData swiftMetaData) {
        return new RealTimeSegmentImpl(location, swiftMetaData);
    }

    @Override
    protected Segment createSegment(int order, String tmpPath) {
        return createSegment(order, Types.StoreType.MEMORY, tmpPath);
    }

    @Override
    public void recordData(Row row, int segIndex) {
        recorder.recordData(row, segIndex);
    }

    @Override
    public void end() {
        recorder.end();
    }
}
